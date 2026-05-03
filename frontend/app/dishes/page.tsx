"use client";

import { useEffect, useState } from "react";
import { PageHeader } from "@/components/layout/page-header";
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
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Plus,
  Pencil,
  Trash2,
  Search,
  UtensilsCrossed,
  BookOpen,
  X,
} from "lucide-react";
import { dishesApi, recipesApi } from "@/lib/api";
import type { Dish, Recipe } from "@/lib/types";

export default function DishesPage() {
  const [dishes, setDishes] = useState<Dish[]>([]);
  const [recipes, setRecipes] = useState<Recipe[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");
  const [dialogOpen, setDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [editingDish, setEditingDish] = useState<Dish | null>(null);
  const [dishToDelete, setDishToDelete] = useState<Dish | null>(null);
  const [selectedDish, setSelectedDish] = useState<Dish | null>(null);
  const [selectedRecipe, setSelectedRecipe] = useState<Recipe | null>(null);
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
      const [dishesData, recipesData] = await Promise.all([
        dishesApi.getAll(),
        recipesApi.getAll(),
      ]);
      setDishes(dishesData);
      setRecipes(recipesData);
    } catch (error) {
      console.error("Failed to load data:", error);
    } finally {
      setLoading(false);
    }
  }

  async function handleSearch() {
    if (!searchQuery.trim()) {
      loadData();
      return;
    }
    try {
      setLoading(true);
      const data = await dishesApi.search(searchQuery);
      setDishes(data);
    } catch (error) {
      console.error("Failed to search dishes:", error);
    } finally {
      setLoading(false);
    }
  }

  function getRecipeName(recipeId: number | null) {
    if (!recipeId) return null;
    return recipes.find((r) => r.id === recipeId)?.name ?? null;
  }

  async function viewRecipe(dish: Dish) {
    if (!dish.recipeId) return;
    setSelectedDish(dish);
    try {
      const recipe = await recipesApi.getById(dish.recipeId);
      setSelectedRecipe(recipe);
    } catch (error) {
      console.error("Failed to load recipe:", error);
    }
  }

  function closeRecipePanel() {
    setSelectedDish(null);
    setSelectedRecipe(null);
  }

  function openCreateDialog() {
    setEditingDish(null);
    setFormData({
      name: "",
      price: "",
      weight: "",
      recipeId: "",
    });
    setDialogOpen(true);
  }

  function openEditDialog(dish: Dish) {
    setEditingDish(dish);
    setFormData({
      name: dish.name,
      price: dish.price.toString(),
      weight: dish.weight.toString(),
      recipeId: dish.recipeId?.toString() ?? "",
    });
    setDialogOpen(true);
  }

  function openDeleteDialog(dish: Dish) {
    setDishToDelete(dish);
    setDeleteDialogOpen(true);
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    try {
      const data = {
        name: formData.name,
        price: parseFloat(formData.price),
        weight: parseFloat(formData.weight),
        recipeId: formData.recipeId ? parseInt(formData.recipeId) : undefined,
      };
      if (editingDish) {
        await dishesApi.update(editingDish.id, data);
      } else {
        await dishesApi.create(data);
      }
      setDialogOpen(false);
      loadData();
    } catch (error) {
      console.error("Failed to save dish:", error);
    }
  }

  async function handleDelete() {
    if (!dishToDelete) return;
    try {
      await dishesApi.delete(dishToDelete.id);
      setDeleteDialogOpen(false);
      setDishToDelete(null);
      loadData();
    } catch (error) {
      console.error("Failed to delete dish:", error);
    }
  }

  return (
    <div className="flex flex-col">
      <PageHeader
        title="Блюда"
        description="Управление блюдами и связь OneToOne с рецептами"
      >
        <Button onClick={openCreateDialog}>
          <Plus className="mr-2 h-4 w-4" />
          Добавить блюдо
        </Button>
      </PageHeader>

      <div className="flex flex-1">
        {/* Main content */}
        <div className="flex-1 space-y-4 p-6">
          {/* Search */}
          <div className="flex gap-2">
            <div className="relative flex-1 max-w-sm">
              <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
              <Input
                placeholder="Поиск по названию..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                onKeyDown={(e) => e.key === "Enter" && handleSearch()}
                className="pl-9"
              />
            </div>
            <Button variant="secondary" onClick={handleSearch}>
              Найти
            </Button>
            <Button
              variant="outline"
              onClick={() => {
                setSearchQuery("");
                loadData();
              }}
            >
              Сбросить
            </Button>
          </div>

          {/* Table */}
          {loading ? (
            <div className="flex h-64 items-center justify-center">
              <p className="text-muted-foreground">Загрузка...</p>
            </div>
          ) : dishes.length === 0 ? (
            <div className="flex h-64 flex-col items-center justify-center gap-2 rounded-lg border border-dashed">
              <UtensilsCrossed className="h-12 w-12 text-muted-foreground" />
              <p className="text-muted-foreground">Блюда не найдены</p>
              <Button variant="outline" onClick={openCreateDialog}>
                Добавить первое блюдо
              </Button>
            </div>
          ) : (
            <div className="rounded-lg border">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>ID</TableHead>
                    <TableHead>Название</TableHead>
                    <TableHead>Цена</TableHead>
                    <TableHead>Вес</TableHead>
                    <TableHead>Рецепт (OneToOne)</TableHead>
                    <TableHead className="text-right">Действия</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {dishes.map((dish) => {
                    const recipeName = getRecipeName(dish.recipeId);
                    return (
                      <TableRow
                        key={dish.id}
                        className={
                          selectedDish?.id === dish.id ? "bg-muted/50" : ""
                        }
                      >
                        <TableCell className="font-medium">{dish.id}</TableCell>
                        <TableCell>{dish.name}</TableCell>
                        <TableCell>{dish.price} руб.</TableCell>
                        <TableCell>{dish.weight} г</TableCell>
                        <TableCell>
                          {recipeName ? (
                            <Button
                              variant="link"
                              className="h-auto p-0 text-primary"
                              onClick={() => viewRecipe(dish)}
                            >
                              <BookOpen className="mr-1 h-4 w-4" />
                              {recipeName}
                            </Button>
                          ) : (
                            <span className="text-muted-foreground">
                              Нет рецепта
                            </span>
                          )}
                        </TableCell>
                        <TableCell className="text-right">
                          <Button
                            variant="ghost"
                            size="icon"
                            onClick={() => openEditDialog(dish)}
                          >
                            <Pencil className="h-4 w-4" />
                          </Button>
                          <Button
                            variant="ghost"
                            size="icon"
                            onClick={() => openDeleteDialog(dish)}
                          >
                            <Trash2 className="h-4 w-4 text-destructive" />
                          </Button>
                        </TableCell>
                      </TableRow>
                    );
                  })}
                </TableBody>
              </Table>
            </div>
          )}
        </div>

        {/* Recipe Panel (OneToOne relationship display) */}
        {selectedDish && selectedRecipe && (
          <div className="w-96 border-l bg-card p-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-semibold">OneToOne: Рецепт блюда</h3>
              <Button variant="ghost" size="icon" onClick={closeRecipePanel}>
                <X className="h-4 w-4" />
              </Button>
            </div>
            <Card>
              <CardHeader>
                <CardTitle className="text-lg">{selectedRecipe.name}</CardTitle>
                <p className="text-sm text-muted-foreground">
                  Для блюда: {selectedDish.name}
                </p>
              </CardHeader>
              <CardContent className="space-y-4">
                <div>
                  <h4 className="text-sm font-medium mb-2">Инструкции</h4>
                  <p className="text-sm text-muted-foreground">
                    {selectedRecipe.instructions || "Нет инструкций"}
                  </p>
                </div>
                {selectedRecipe.ingredients &&
                  selectedRecipe.ingredients.length > 0 && (
                    <div>
                      <h4 className="text-sm font-medium mb-2">
                        Ингредиенты ({selectedRecipe.ingredients.length})
                      </h4>
                      <ul className="space-y-1">
                        {selectedRecipe.ingredients.map((ing) => (
                          <li
                            key={ing.id}
                            className="text-sm text-muted-foreground"
                          >
                            Продукт #{ing.productId}: {ing.quantity} ед.
                          </li>
                        ))}
                      </ul>
                    </div>
                  )}
              </CardContent>
            </Card>
          </div>
        )}
      </div>

      {/* Create/Edit Dialog */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>
              {editingDish ? "Редактировать блюдо" : "Новое блюдо"}
            </DialogTitle>
          </DialogHeader>
          <form onSubmit={handleSubmit}>
            <div className="space-y-4 py-4">
              <div className="space-y-2">
                <Label htmlFor="name">Название</Label>
                <Input
                  id="name"
                  value={formData.name}
                  onChange={(e) =>
                    setFormData({ ...formData, name: e.target.value })
                  }
                  placeholder="Введите название блюда"
                  required
                />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="price">Цена (руб.)</Label>
                  <Input
                    id="price"
                    type="number"
                    step="0.01"
                    value={formData.price}
                    onChange={(e) =>
                      setFormData({ ...formData, price: e.target.value })
                    }
                    placeholder="0.00"
                    required
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="weight">Вес (г)</Label>
                  <Input
                    id="weight"
                    type="number"
                    value={formData.weight}
                    onChange={(e) =>
                      setFormData({ ...formData, weight: e.target.value })
                    }
                    placeholder="0"
                    required
                  />
                </div>
              </div>
              <div className="space-y-2">
                <Label htmlFor="recipe">Рецепт (OneToOne связь)</Label>
                <Select
                  value={formData.recipeId}
                  onValueChange={(value) =>
                    setFormData({ ...formData, recipeId: value })
                  }
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Выберите рецепт (опционально)" />
                  </SelectTrigger>
                  <SelectContent>
                    {recipes.map((recipe) => (
                      <SelectItem key={recipe.id} value={recipe.id.toString()}>
                        {recipe.name}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
            </div>
            <DialogFooter>
              <Button
                type="button"
                variant="outline"
                onClick={() => setDialogOpen(false)}
              >
                Отмена
              </Button>
              <Button type="submit">
                {editingDish ? "Сохранить" : "Создать"}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <Dialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Удалить блюдо?</DialogTitle>
          </DialogHeader>
          <p className="text-sm text-muted-foreground">
            Вы уверены, что хотите удалить блюдо &quot;{dishToDelete?.name}&quot;? Это
            действие нельзя отменить.
          </p>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setDeleteDialogOpen(false)}
            >
              Отмена
            </Button>
            <Button variant="destructive" onClick={handleDelete}>
              Удалить
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
