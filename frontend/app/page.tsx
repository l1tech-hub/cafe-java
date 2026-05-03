"use client";

import Link from "next/link";
import { useCallback, useEffect, useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { PageHeader } from "@/components/layout/page-header";
import { Button } from "@/components/ui/button";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  UtensilsCrossed,
  Package,
  BookOpen,
  Boxes,
  Trash2,
} from "lucide-react";
import { dishesApi, productsApi, recipesApi, batchesApi } from "@/lib/api";
import type { DishCookStat, Batch, ProductSpent } from "@/lib/types";

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString("ru");
}

function fmtNum(n: number | null | undefined, fraction = 2) {
  if (n == null || Number.isNaN(n)) return "—";
  return n.toFixed(fraction);
}

export default function DashboardPage() {
  const [stats, setStats] = useState({
    dishes: 0,
    products: 0,
    recipes: 0,
    batches: 0,
  });
  const [expiredBatches, setExpiredBatches] = useState<Batch[]>([]);
  const [cookStats, setCookStats] = useState<DishCookStat[]>([]);
  const [spentProducts, setSpentProducts] = useState<ProductSpent[]>([]);
  const [loading, setLoading] = useState(true);

  const loadData = useCallback(async () => {
    try {
      setLoading(true);
      const [dishes, products, recipes, batches, expired, cookingStatistics, spent] =
        await Promise.all([
          dishesApi.getAll(),
          productsApi.getAll(),
          recipesApi.getAll(),
          batchesApi.getAll(),
          batchesApi.getExpired(),
          dishesApi.getCookingStatistics(),
          dishesApi.getSpentProductsKilograms(),
        ]);

      setStats({
        dishes: dishes.length,
        products: products.length,
        recipes: recipes.length,
        batches: batches.length,
      });
      setExpiredBatches(expired);
      setCookStats(cookingStatistics);
      setSpentProducts(spent);
    } catch (error) {
      console.error("Failed to load admin data:", error);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadData();
  }, [loadData]);

  async function handleDeleteBatch(id: number) {
    try {
      await batchesApi.delete(id);
      await loadData();
    } catch (error) {
      console.error("Failed to delete batch:", error);
    }
  }

  const statCards = [
    {
      title: "Блюда",
      value: stats.dishes,
      icon: UtensilsCrossed,
      href: "/dishes",
    },
    {
      title: "Продукты",
      value: stats.products,
      icon: Package,
      href: "/products",
    },
    {
      title: "Рецепты",
      value: stats.recipes,
      icon: BookOpen,
      href: "/recipes",
    },
    {
      title: "Партии",
      value: stats.batches,
      icon: Boxes,
      href: "/batches",
    },
  ];

  const totalOutputMassKg = cookStats.reduce(
    (sum, row) => sum + (row.totalPortionsMassKilograms ?? 0),
    0
  );

  if (loading) {
    return (
      <div className="flex h-full items-center justify-center">
        <div className="text-muted-foreground">Загрузка...</div>
      </div>
    );
  }

  return (
    <div className="flex flex-col">
      <PageHeader
        title="Администрирование"
        description="Статистика приготовленных блюд и просроченые продукты"
      />

      <div className="flex-1 space-y-6 p-6">
        <div className="grid gap-2 sm:grid-cols-2 lg:grid-cols-4">
          {statCards.map((stat) => (
            <Link key={stat.title} href={stat.href} className="block">
              <Card className="h-full transition-colors hover:bg-muted/50">
                <CardHeader className="flex flex-row items-center justify-between space-y-0 p-3 pb-2">
                  <CardTitle className="text-xs font-medium leading-tight">
                    {stat.title}
                  </CardTitle>
                  <stat.icon className="h-3.5 w-3.5 shrink-0 text-muted-foreground" />
                </CardHeader>
                <CardContent className="p-3 pt-0">
                  <div className="text-lg font-bold tabular-nums leading-none">
                    {stat.value}
                  </div>
                </CardContent>
              </Card>
            </Link>
          ))}
        </div>

        <Card>
          <CardHeader className="pb-3">
            <CardTitle className="text-base">Просроченные партии</CardTitle>
          </CardHeader>
          <CardContent>
            {expiredBatches.length === 0 ? (
              <p className="text-sm text-muted-foreground">Нет просроченных партий</p>
            ) : (
              <div className="rounded-md border overflow-x-auto">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Продукт</TableHead>
                      <TableHead>ID партии</TableHead>
                      <TableHead>ID продукта</TableHead>
                      <TableHead className="text-right">Кол-во, г</TableHead>
                      <TableHead className="text-right">Цена за кг</TableHead>
                      <TableHead>Производство</TableHead>
                      <TableHead>Годен до</TableHead>
                      <TableHead className="w-[100px] text-right">Действия</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {expiredBatches.map((batch) => (
                      <TableRow key={batch.id}>
                        <TableCell className="font-medium">
                          {batch.productName ?? "—"}
                        </TableCell>
                        <TableCell>{batch.id}</TableCell>
                        <TableCell>{batch.productId}</TableCell>
                        <TableCell className="text-right tabular-nums">
                          {batch.quantity}
                        </TableCell>
                        <TableCell className="text-right tabular-nums">
                          {batch.price}
                        </TableCell>
                        <TableCell>{formatDate(batch.manufactureDate)}</TableCell>
                        <TableCell>{formatDate(batch.expiryDate)}</TableCell>
                        <TableCell className="text-right">
                          <Button
                            type="button"
                            variant="ghost"
                            size="icon"
                            className="text-destructive hover:text-destructive"
                            onClick={() => handleDeleteBatch(batch.id)}
                            aria-label="Удалить партию"
                          >
                            <Trash2 className="h-4 w-4" />
                          </Button>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-3">
            <CardTitle className="text-base">Приготовленные блюда</CardTitle>
          </CardHeader>
          <CardContent>
            {cookStats.length === 0 ? (
              <p className="text-sm text-muted-foreground">
                Пока нет записей — приготовьте блюдо на странице «Расчёт и приготовление»
              </p>
            ) : (
              <div className="rounded-md border overflow-x-auto">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Блюдо</TableHead>
                      <TableHead className="text-right">Цена, руб.</TableHead>
                      <TableHead className="text-right">Вес порции, г</TableHead>
                      <TableHead className="text-right">Приготовлений</TableHead>
                      <TableHead className="text-right">Стоимость всех порций, руб.</TableHead>
                      <TableHead className="text-right">
                        Масса всех порций, кг
                      </TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {cookStats.map((row) => (
                      <TableRow key={row.dishId}>
                        <TableCell className="font-medium">{row.dishName}</TableCell>
                        <TableCell className="text-right tabular-nums">
                          {fmtNum(row.dishPrice)}
                        </TableCell>
                        <TableCell className="text-right tabular-nums">
                          {fmtNum(row.dishWeightGrams)}
                        </TableCell>
                        <TableCell className="text-right tabular-nums">
                          {row.cookCount}
                        </TableCell>
                        <TableCell className="text-right tabular-nums">
                          {fmtNum(row.totalIterationsPrice)}
                        </TableCell>
                        <TableCell className="text-right tabular-nums">
                          {fmtNum(row.totalPortionsMassKilograms)}
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-3">
            <CardTitle className="text-base">Потраченные продукты</CardTitle>
          </CardHeader>
          <CardContent>
            {spentProducts.length === 0 ? (
              <p className="text-sm text-muted-foreground">
                Нет данных по рецептам приготовленных блюд
              </p>
            ) : (
              <div className="rounded-md border overflow-x-auto">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Продукт</TableHead>
                      <TableHead className="text-right">Затрачено, кг</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {spentProducts.map((row, idx) => (
                      <TableRow key={`${row.productName}-${idx}`}>
                        <TableCell className="font-medium">{row.productName}</TableCell>
                        <TableCell className="text-right tabular-nums">
                          {row.spentKilograms.toFixed(3)}
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
            )}
            <p className="mt-3 text-sm text-muted-foreground">
              Суммарная масса готовых порций (число приготовлений × вес одной порции):{" "}
              <span className="font-medium text-foreground tabular-nums">
                {totalOutputMassKg.toFixed(3)} кг
              </span>
            </p>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
