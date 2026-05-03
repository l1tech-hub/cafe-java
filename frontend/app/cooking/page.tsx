"use client";

import { useEffect, useMemo, useState } from "react";
import { PageHeader } from "@/components/layout/page-header";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { dishesApi, ingredientsApi, recipesApi } from "@/lib/api";
import { cn } from "@/lib/utils";
import type {
  BatchOrder,
  Dish,
  IngredientMissing,
  Recipe,
  RecipeCostEstimate,
} from "@/lib/types";

const BATCH_ORDER_OPTIONS: { value: BatchOrder; label: string }[] = [
  { value: "PRICE_ASC", label: "По возрастанию цены" },
  { value: "PRICE_DESC", label: "По убыванию цены" },
  { value: "EXPIRY_ASC", label: "По возрастанию срока годности" },
  { value: "EXPIRY_DESC", label: "По убыванию срока годности" },
];

function sleep(ms: number) {
  return new Promise<void>((resolve) => setTimeout(resolve, ms));
}

function todayIsoDateLocal() {
  const d = new Date();
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${y}-${m}-${day}`;
}

function mapCookErrorMessage(raw: string | null | undefined): string {
  const message = raw?.trim() ?? "";
  if (!message) return "Приготовление завершилось с ошибкой.";

  if (message.includes("Cooking from expired batches is not allowed")) {
    const m = message.match(/Products '([^']+)'/);
    const product = m?.[1];
    return product
      ? `Нельзя использовать просроченные партии: для «${product}» не хватает непросрочного запаса (включите флаг или пополните склад).`
      : "Нельзя использовать просроченные партии: не хватает непросрочных партий.";
  }

  const missing = message.match(/^Missing ([\d.,]+) of '([^']+)'$/);
  if (missing) {
    const [, amount, product] = missing;
    return `Недостаточно продукта «${product}»: не хватает ${amount.replace(",", ".")}.`;
  }

  return message;
}

async function waitForCookTask(taskId: string) {
  const deadline = Date.now() + 120_000;
  while (Date.now() < deadline) {
    const st = await dishesApi.getCookTaskStatus(taskId);
    if (st.status === "DONE" || st.status === "FAILED") return st;
    await sleep(400);
  }
  return {
    id: taskId,
    status: "FAILED" as const,
    message: "Превышено время ожидания приготовления.",
  };
}

type MissingBlock = {
  recipeId: number;
  dishSummary: string;
  iterations: number;
  rows: IngredientMissing[];
};

type CostSection = {
  recipeId: number;
  dishSummary: string;
  iterations: number;
  estimate: RecipeCostEstimate;
};

export default function CookingPage() {
  const [dishes, setDishes] = useState<Dish[]>([]);
  const [recipesById, setRecipesById] = useState<Record<number, Recipe>>({});
  const [dishQuantities, setDishQuantities] = useState<Record<number, string>>({});
  const [batchOrder, setBatchOrder] = useState<BatchOrder>("EXPIRY_ASC");
  const [allowExpiredProducts, setAllowExpiredProducts] = useState(false);
  const [loading, setLoading] = useState(true);
  const [isCooking, setIsCooking] = useState(false);
  const [isCalculating, setIsCalculating] = useState(false);
  const [cookResult, setCookResult] = useState<string>("");
  const [cookError, setCookError] = useState<string>("");
  const [missingBlocks, setMissingBlocks] = useState<MissingBlock[]>([]);
  const [missingError, setMissingError] = useState<string>("");
  const [costSections, setCostSections] = useState<CostSection[]>([]);

  useEffect(() => {
    setCostSections([]);
  }, [batchOrder]);

  useEffect(() => {
    async function loadData() {
      try {
        setLoading(true);
        const dishesData = await dishesApi.getAll();
        setDishes(dishesData);
      } catch (error) {
        console.error("Failed to load cooking data:", error);
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, []);

  const selectedDishes = useMemo(
    () =>
      dishes.filter((dish) => {
        const qty = parseInt(dishQuantities[dish.id] || "0", 10);
        return !Number.isNaN(qty) && qty > 0;
      }),
    [dishes, dishQuantities]
  );

  const grandTotalCost = useMemo(
    () => costSections.reduce((sum, s) => sum + (s.estimate.totalCost ?? 0), 0),
    [costSections]
  );

  async function handleCalculateMissing() {
    setMissingBlocks([]);
    setMissingError("");
    setCostSections([]);
    if (selectedDishes.length === 0) {
      setMissingError("Укажите целое количество порций хотя бы для одного блюда.");
      return;
    }

    const date = todayIsoDateLocal();
    const byRecipe = new Map<number, { iterations: number; dishes: string[] }>();

    for (const dish of selectedDishes) {
      if (!dish.recipeId) continue;
      const portions = parseInt(dishQuantities[dish.id] || "0", 10);
      const cur = byRecipe.get(dish.recipeId) ?? { iterations: 0, dishes: [] };
      cur.iterations += portions;
      cur.dishes.push(`${dish.name} (×${portions})`);
      byRecipe.set(dish.recipeId, cur);
    }

    if (byRecipe.size === 0) {
      setMissingError("У выбранных блюд нет привязанного рецепта — рассчитать нечего.");
      return;
    }

    try {
      setIsCalculating(true);
      const recipeIds = [...byRecipe.keys()];
      const loadedRecipes = await Promise.all(
        recipeIds.map((id) => recipesApi.getById(id))
      );
      setRecipesById((prev) => {
        const next = { ...prev };
        for (const r of loadedRecipes) next[r.id] = r;
        return next;
      });

      const blocks: MissingBlock[] = [];
      for (const [recipeId, { iterations, dishes: dishLabels }] of byRecipe) {
        const rows = await ingredientsApi.getMissing(recipeId, iterations, date);
        blocks.push({
          recipeId,
          dishSummary: [...new Set(dishLabels)].join(", "),
          iterations,
          rows,
        });
      }
      setMissingBlocks(blocks);

      const allSufficient = blocks.every((b) => b.rows.length === 0);
      if (allSufficient) {
        const sections: CostSection[] = await Promise.all(
          blocks.map(async (b) => ({
            recipeId: b.recipeId,
            dishSummary: b.dishSummary,
            iterations: b.iterations,
            estimate: await recipesApi.getCostEstimate(
              b.recipeId,
              b.iterations,
              date,
              batchOrder
            ),
          }))
        );
        setCostSections(sections);
      }
    } catch (error) {
      console.error("calculate failed:", error);
      setMissingError("Не удалось выполнить расчёт.");
    } finally {
      setIsCalculating(false);
    }
  }

  async function handleCook() {
    try {
      setIsCooking(true);
      setCookResult("");
      setCookError("");

      const startPromises: Promise<string>[] = [];
      for (const dish of selectedDishes) {
        const portions = parseInt(dishQuantities[dish.id] || "0", 10);
        if (Number.isNaN(portions) || portions <= 0) continue;
        for (let i = 0; i < portions; i += 1) {
          startPromises.push(
            dishesApi.cook(dish.id, allowExpiredProducts, batchOrder)
          );
        }
      }

      if (startPromises.length === 0) {
        setCookResult("Нет блюд для приготовления");
        return;
      }

      const taskIds = await Promise.all(startPromises);
      const results = await Promise.all(taskIds.map((id) => waitForCookTask(id)));

      const failed = results.filter((r) => r.status === "FAILED");
      const done = results.filter((r) => r.status === "DONE");

      if (failed.length > 0) {
        const lines = [...new Set(failed.map((f) => mapCookErrorMessage(f.message)))];
        setCookError(lines.join(" "));
      }

      if (done.length > 0) {
        setCookResult(`Успешно приготовлено порций: ${done.length}.`);
      } else if (failed.length === 0) {
        setCookResult("");
      }
    } catch (error) {
      console.error("Failed to start cooking:", error);
      setCookError("Не удалось запустить приготовление.");
    } finally {
      setIsCooking(false);
    }
  }

  return (
    <div className="relative flex min-h-full flex-col pb-40">
      <PageHeader
        title="Расчёт и приготовление"
        description="Выбор блюд, расчёт ингредиентов и запуск приготовления"
      />

      <div className="flex-1 space-y-4 p-6">
        {loading ? (
          <p className="text-muted-foreground">Загрузка...</p>
        ) : (
          <>
            <Card>
              <CardHeader>
                <CardTitle>Блюда для приготовления</CardTitle>
              </CardHeader>
              <CardContent className="space-y-3">
                {dishes.map((dish) => {
                  const qty = parseInt(dishQuantities[dish.id] || "0", 10);
                  const active = !Number.isNaN(qty) && qty > 0;
                  return (
                    <div
                      key={dish.id}
                      className={cn(
                        "flex items-center gap-3 rounded-lg border p-3 transition-colors",
                        active && "bg-muted/60 ring-1 ring-primary/40"
                      )}
                    >
                      <span className="min-w-56 font-medium">{dish.name}</span>
                      <div className="flex items-center gap-2">
                        <Label htmlFor={`qty-${dish.id}`}>Кол-во</Label>
                        <Input
                          id={`qty-${dish.id}`}
                          className="w-28"
                          type="number"
                          min={0}
                          step={1}
                          inputMode="numeric"
                          value={dishQuantities[dish.id] ?? ""}
                          onChange={(e) => {
                            const value = e.target.value;
                            if (value === "") {
                              setDishQuantities((prev) => ({ ...prev, [dish.id]: "" }));
                              return;
                            }
                            const n = parseInt(value, 10);
                            if (Number.isNaN(n)) return;
                            const clamped = Math.max(0, n);
                            setDishQuantities((prev) => ({
                              ...prev,
                              [dish.id]: String(clamped),
                            }));
                          }}
                          placeholder="0"
                        />
                      </div>
                    </div>
                  );
                })}
              </CardContent>
            </Card>

            {missingBlocks.length > 0 ? (
              <Card>
                <CardHeader>
                  <CardTitle>Недостающие продукты (по складу)</CardTitle>
                </CardHeader>
                <CardContent className="space-y-6">
                  {missingBlocks.map((block) => {
                    const recipeTitle =
                      recipesById[block.recipeId]?.name ?? `Рецепт #${block.recipeId}`;
                    return (
                      <div key={block.recipeId} className="space-y-2">
                        <p className="text-sm font-medium">
                          {recipeTitle} — {block.dishSummary} (итераций: {block.iterations})
                        </p>
                        {block.rows.length === 0 ? (
                          <p className="text-sm text-muted-foreground">
                            Запасов достаточно
                          </p>
                        ) : (
                          <ul className="space-y-1 border-l-2 border-destructive/40 pl-3">
                            {block.rows.map((row) => (
                              <li key={row.ingredientId} className="text-sm">
                                <span className="font-medium">{row.productName}</span>
                                {" — "}
                                требуется {row.required?.toFixed(2) ?? "—"} г, на складе{" "}
                                {row.available?.toFixed(2) ?? "—"} г, не хватает{" "}
                                <span className="text-destructive">
                                  {row.missing?.toFixed(2) ?? "—"} г
                                </span>
                              </li>
                            ))}
                          </ul>
                        )}
                      </div>
                    );
                  })}
                </CardContent>
              </Card>
            ) : null}

            {costSections.length > 0 ? (
              <Card>
                <CardHeader>
                  <CardTitle>Рассчёт стоимости</CardTitle>
                </CardHeader>
                <CardContent className="space-y-6">
                  {costSections.map((section) => {
                    const recipeTitle =
                      recipesById[section.recipeId]?.name ??
                      `Рецепт #${section.recipeId}`;
                    return (
                      <div key={section.recipeId} className="space-y-2 border-b pb-4 last:border-0">
                        <p className="text-sm font-medium">
                          {recipeTitle} — {section.dishSummary}
                        </p>
                        <ul className="space-y-1">
                          {section.estimate.lines.map((line) => (
                            <li key={line.ingredientId} className="text-sm">
                              {line.productName}: {line.quantity.toFixed(2)} г —{" "}
                              {(line.cost ?? 0).toFixed(2)} руб.
                            </li>
                          ))}
                        </ul>
                        <p className="text-sm font-medium">
                          Стоимость по рецепту: {(section.estimate.totalCost ?? 0).toFixed(2)}{" "}
                          руб.
                        </p>
                      </div>
                    );
                  })}
                  <p className="text-base font-semibold">
                    Итого по выбранным блюдам: {grandTotalCost.toFixed(2)} руб.
                  </p>
                </CardContent>
              </Card>
            ) : null}

            {missingError ? (
              <p className="text-sm font-medium text-destructive">{missingError}</p>
            ) : null}
          </>
        )}
      </div>

      <div className="fixed bottom-0 left-64 right-0 z-20 border-t bg-background/95 px-4 py-3 shadow-md backdrop-blur supports-[backdrop-filter]:bg-background/80">
        <div className="flex flex-col gap-3 lg:flex-row lg:items-center lg:justify-between">
          <div className="flex flex-col gap-2 sm:flex-row sm:flex-wrap sm:items-center sm:gap-4">
            <div className="flex min-w-[220px] flex-col gap-1">
              <Label className="text-xs text-muted-foreground">Порядок выбора партий</Label>
              <Select
                value={batchOrder}
                onValueChange={(v) => setBatchOrder(v as BatchOrder)}
              >
                <SelectTrigger className="w-full sm:w-[280px]">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {BATCH_ORDER_OPTIONS.map((opt) => (
                    <SelectItem key={opt.value} value={opt.value}>
                      {opt.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="flex items-center gap-2">
              <input
                id="allowExpiredProducts"
                type="checkbox"
                checked={allowExpiredProducts}
                onChange={(e) => setAllowExpiredProducts(e.target.checked)}
              />
              <Label htmlFor="allowExpiredProducts" className="cursor-pointer text-sm font-normal">
                Использовать просроченные партии
              </Label>
            </div>
          </div>
          <div className="flex flex-wrap gap-2">
            <Button
              type="button"
              variant="secondary"
              onClick={handleCalculateMissing}
              disabled={loading || isCalculating || selectedDishes.length === 0}
            >
              {isCalculating ? "Считаем…" : "Рассчитать продукты и цену"}
            </Button>
            <Button
              type="button"
              onClick={handleCook}
              disabled={loading || isCooking || selectedDishes.length === 0}
            >
              {isCooking ? "Готовим…" : "Приготовить"}
            </Button>
          </div>
        </div>
        {cookError ? (
          <p className="mt-2 text-sm font-medium text-destructive">{cookError}</p>
        ) : null}
        {cookResult ? (
          <p className="mt-1 text-sm text-muted-foreground">{cookResult}</p>
        ) : null}
      </div>
    </div>
  );
}
