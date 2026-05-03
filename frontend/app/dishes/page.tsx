"use client";

import { Fragment, useEffect, useState } from "react";
import { PageHeader } from "@/components/layout/page-header";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  Plus,
  Pencil,
  Trash2,
  Search,
  UtensilsCrossed,
  ChevronDown,
  ChevronRight,
} from "lucide-react";
import { dishesApi, recipesApi, productsApi } from "@/lib/api";
import type { Dish, Product, Recipe } from "@/lib/types";

export default function DishesPage() {
  const [dishes, setDishes] = useState<Dish[]>([]);
  const [recipes, setRecipes] = useState<Recipe[]>([]);
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);

  const [searchQuery, setSearchQuery] = useState("");

  const [sortType, setSortType] = useState<string>("none");

  const [dialogOpen, setDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [editingDish, setEditingDish] = useState<Dish | null>(null);
  const [dishToDelete, setDishToDelete] = useState<Dish | null>(null);

  const [expandedDish, setExpandedDish] = useState<number | null>(null);
  const [recipesById, setRecipesById] = useState<Record<number, Recipe>>({});

  const [formData, setFormData] = useState({
    name: "",
    price: "",
    weight: "",
    recipeId: "",
  });

  useEffect(() => {
    loadData();
  }, []);

  async function loadData() {
    try {
      setLoading(true);
      const [dishesData, recipesData, productsData] = await Promise.all([
        dishesApi.getAll(),
        recipesApi.getAll(),
        productsApi.getAll(),
      ]);
      setDishes(dishesData);
      setRecipes(recipesData);
      setProducts(productsData);
    } finally {
      setLoading(false);
    }
  }

  async function handleSearch() {
    if (!searchQuery.trim()) {
      loadData();
      return;
    }

    setLoading(true);
    const data = await dishesApi.search(searchQuery);
    setDishes(data);
    setLoading(false);
  }

  function getRecipeName(recipeId: number | null) {
    if (!recipeId) return null;
    return recipes.find((r) => r.id === recipeId)?.name ?? null;
  }

  function getProductName(productId: number) {
    return (
        products.find((p) => p.id === productId)?.name ??
        `Продукт #${productId}`
    );
  }

  async function toggleRecipe(dish: Dish) {
    if (!dish.recipeId) return;

    if (expandedDish === dish.id) {
      setExpandedDish(null);
      return;
    }

    if (!recipesById[dish.recipeId]) {
      const recipe = await recipesApi.getById(dish.recipeId);
      setRecipesById((prev) => ({ ...prev, [recipe.id]: recipe }));
    }

    setExpandedDish(dish.id);
  }

  const filteredDishes = dishes
  .filter((dish) => true)
  .sort((a, b) => {
    switch (sortType) {
      case "price_asc":
        return a.price - b.price;
      case "price_desc":
        return b.price - a.price;
      case "weight_asc":
        return a.weight - b.weight;
      case "weight_desc":
        return b.weight - a.weight;
      default:
        return 0;
    }
  });

  function openCreateDialog() {
    setEditingDish(null);
    setFormData({
      name: "",
      price: "",
      weight: "",
      recipeId: "none",
    });
    setDialogOpen(true);
  }

  function openEditDialog(dish: Dish) {
    setEditingDish(dish);
    setFormData({
      name: dish.name,
      price: String(dish.price),
      weight: String(dish.weight),
      recipeId: dish.recipeId ? String(dish.recipeId) : "none",
    });
    setDialogOpen(true);
  }

  function openDeleteDialog(dish: Dish) {
    setDishToDelete(dish);
    setDeleteDialogOpen(true);
  }

  async function handleDelete() {
    if (!dishToDelete) return;
    await dishesApi.delete(dishToDelete.id);
    setDeleteDialogOpen(false);
    setDishToDelete(null);
    loadData();
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();

    const data = {
      name: formData.name,
      price: Number(formData.price),
      weight: Number(formData.weight),
      recipeId:
          formData.recipeId && formData.recipeId !== "none"
              ? Number(formData.recipeId)
              : undefined,
    };

    if (editingDish) {
      await dishesApi.update(editingDish.id, data);
    } else {
      await dishesApi.create(data);
    }

    setDialogOpen(false);
    loadData();
  }

  return (
      <div className="flex flex-col">
        <PageHeader title="Блюда" description="Управление блюдами и рецептами">
          <Button onClick={openCreateDialog}>
            <Plus className="mr-2 h-4 w-4" />
            Добавить блюдо
          </Button>
        </PageHeader>

        <div className="flex-1 space-y-4 p-6">
          {/* Search + Sort */}
          <div className="flex flex-wrap gap-2 items-center">
            <div className="relative flex-1 max-w-sm">
              <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
              <Input
                  placeholder="Поиск..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  onKeyDown={(e) => e.key === "Enter" && handleSearch()}
                  className="pl-9"
              />
            </div>

            <Button onClick={handleSearch}>Найти</Button>

            <Button
                variant="outline"
                onClick={() => {
                  setSearchQuery("");
                  setSortType("none");
                  loadData();
                }}
            >
              Сбросить
            </Button>

            <Select value={sortType} onValueChange={setSortType}>
              <SelectTrigger className="w-[220px]">
                <SelectValue placeholder="Сортировка" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="none">Без сортировки</SelectItem>
                <SelectItem value="price_asc">Цена ↑</SelectItem>
                <SelectItem value="price_desc">Цена ↓</SelectItem>
                <SelectItem value="weight_asc">Вес ↑</SelectItem>
                <SelectItem value="weight_desc">Вес ↓</SelectItem>
              </SelectContent>
            </Select>
          </div>

          {/* Table */}
          {loading ? (
              <div>Загрузка...</div>
          ) : filteredDishes.length === 0 ? (
              <div className="flex h-64 items-center justify-center gap-2">
                <UtensilsCrossed />
                <span>Нет данных</span>
              </div>
          ) : (
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead />
                    <TableHead>Название</TableHead>
                    <TableHead>Цена</TableHead>
                    <TableHead>Вес</TableHead>
                    <TableHead>Рецепт</TableHead>
                    <TableHead>Действия</TableHead>
                  </TableRow>
                </TableHeader>

                <TableBody>
                  {filteredDishes.map((dish) => {
                    const recipe = dish.recipeId
                        ? recipesById[dish.recipeId]
                        : null;

                    return (
                        <Fragment key={dish.id}>
                          <TableRow>
                            <TableCell>
                              <Button
                                  variant="ghost"
                                  size="icon"
                                  onClick={() => toggleRecipe(dish)}
                              >
                                {expandedDish === dish.id ? (
                                    <ChevronDown />
                                ) : (
                                    <ChevronRight />
                                )}
                              </Button>
                            </TableCell>

                            <TableCell>{dish.name}</TableCell>
                            <TableCell>{dish.price}</TableCell>
                            <TableCell>{dish.weight}</TableCell>
                            <TableCell>{getRecipeName(dish.recipeId) ?? "—"}</TableCell>

                            <TableCell>
                              <Button
                                  variant="ghost"
                                  size="icon"
                                  onClick={() => openEditDialog(dish)}
                              >
                                <Pencil />
                              </Button>

                              <Button
                                  variant="ghost"
                                  size="icon"
                                  onClick={() => openDeleteDialog(dish)}
                              >
                                <Trash2 />
                              </Button>
                            </TableCell>
                          </TableRow>

                          {expandedDish === dish.id && (
                              <TableRow>
                                <TableCell colSpan={6}>
                                  {!recipe ? (
                                      "Загрузка..."
                                  ) : (
                                      <div>
                                        {recipe.ingredients.map((i) => (
                                            <div key={i.id}>
                                              {getProductName(i.productId)} — {i.quantity} г
                                            </div>
                                        ))}
                                      </div>
                                  )}
                                </TableCell>
                              </TableRow>
                          )}
                        </Fragment>
                    );
                  })}
                </TableBody>
              </Table>
          )}
        </div>

        {/* dialogs omitted unchanged */}
      </div>
  );
}
