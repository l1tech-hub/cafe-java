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
  BookOpen,
  X,
  ChevronLeft,
  ChevronRight,
  Layers,
} from "lucide-react";
import { recipesApi, dishesApi, productsApi, ingredientsApi } from "@/lib/api";
import type { Recipe, Dish, Product, Ingredient } from "@/lib/types";

export default function RecipesPage() {
  const [recipes, setRecipes] = useState<Recipe[]>([]);
  const [dishes, setDishes] = useState<Dish[]>([]);
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [ingredientDialogOpen, setIngredientDialogOpen] = useState(false);
  const [editingRecipe, setEditingRecipe] = useState<Recipe | null>(null);
  const [recipeToDelete, setRecipeToDelete] = useState<Recipe | null>(null);
  const [selectedRecipe, setSelectedRecipe] = useState<Recipe | null>(null);
  const [recipeIngredients, setRecipeIngredients] = useState<Ingredient[]>([]);
  const [formData, setFormData] = useState({
    name: "",
    instructions: "",
    dishId: "",
  });
  const [ingredientForm, setIngredientForm] = useState({
    productId: "",
    quantity: "",
  });
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 10;

  useEffect(() => {
    loadData();
  }, [page]);

  async function loadData() {
    try {
      setLoading(true);
      const [recipesData, dishesData, productsData] = await Promise.all([
        recipesApi.getPaged(page, pageSize),
        dishesApi.getAll(),
        productsApi.getAll(),
      ]);
      setRecipes(recipesData.content);
      setTotalPages(recipesData.totalPages);
      setDishes(dishesData);
      setProducts(productsData);
    } catch (error) {
      console.error("Failed to load data:", error);
    } finally {
      setLoading(false);
    }
  }

  function getDishName(dishId: number | null) {
    if (!dishId) return null;
    return dishes.find((d) => d.id === dishId)?.name ?? null;
  }

  function getProductName(productId: number) {
    return products.find((p) => p.id === productId)?.name ?? `ID: ${productId}`;
  }

  async function viewIngredients(recipe: Recipe) {
    setSelectedRecipe(recipe);
    try {
      const ingredients = await ingredientsApi.getByRecipe(recipe.id);
      setRecipeIngredients(ingredients);
    } catch (error) {
      console.error("Failed to load ingredients:", error);
      setRecipeIngredients([]);
    }
  }

  function closeIngredientsPanel() {
    setSelectedRecipe(null);
    setRecipeIngredients([]);
  }

  function openCreateDialog() {
    setEditingRecipe(null);
    setFormData({
      name: "",
      instructions: "",
      dishId: "",
    });
    setDialogOpen(true);
  }

  function openEditDialog(recipe: Recipe) {
    setEditingRecipe(recipe);
    setFormData({
      name: recipe.name,
      instructions: recipe.instructions,
      dishId: recipe.dishId?.toString() ?? "",
    });
    setDialogOpen(true);
  }

  function openDeleteDialog(recipe: Recipe) {
    setRecipeToDelete(recipe);
    setDeleteDialogOpen(true);
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    try {
      const data = {
        name: formData.name,
        instructions: formData.instructions,
        dishId: formData.dishId ? parseInt(formData.dishId) : undefined,
        ingredients: [],
      };
      if (editingRecipe) {
        await recipesApi.update(editingRecipe.id, data);
      } else {
        await recipesApi.create(data);
      }
      setDialogOpen(false);
      loadData();
    } catch (error) {
      console.error("Failed to save recipe:", error);
    }
  }

  async function handleDelete() {
    if (!recipeToDelete) return;
    try {
      await recipesApi.delete(recipeToDelete.id);
      setDeleteDialogOpen(false);
      setRecipeToDelete(null);
      if (selectedRecipe?.id === recipeToDelete.id) {
        closeIngredientsPanel();
      }
      loadData();
    } catch (error) {
      console.error("Failed to delete recipe:", error);
    }
  }

  function openAddIngredientDialog() {
    setIngredientForm({ productId: "", quantity: "" });
    setIngredientDialogOpen(true);
  }

  async function handleAddIngredient(e: React.FormEvent) {
    e.preventDefault();
    if (!selectedRecipe) return;
    try {
      await ingredientsApi.create(selectedRecipe.id, {
        productId: parseInt(ingredientForm.productId),
        quantity: parseFloat(ingredientForm.quantity),
      });
      setIngredientDialogOpen(false);
      // Reload ingredients
      const ingredients = await ingredientsApi.getByRecipe(selectedRecipe.id);
      setRecipeIngredients(ingredients);
    } catch (error) {
      console.error("Failed to add ingredient:", error);
    }
  }

  async function handleDeleteIngredient(ingredientId: number) {
    if (!selectedRecipe) return;
    try {
      await ingredientsApi.delete(ingredientId);
      const ingredients = await ingredientsApi.getByRecipe(selectedRecipe.id);
      setRecipeIngredients(ingredients);
    } catch (error) {
      console.error("Failed to delete ingredient:", error);
    }
  }

  return (
    <div className="flex flex-col">
      <PageHeader
        title="Рецепты"
        description="Управление рецептами и связь OneToMany с ингредиентами"
      >
        <Button onClick={openCreateDialog}>
          <Plus className="mr-2 h-4 w-4" />
          Добавить рецепт
        </Button>
      </PageHeader>

      <div className="flex flex-1">
        {/* Main content */}
        <div className="flex-1 space-y-4 p-6">
          {/* Table */}
          {loading ? (
            <div className="flex h-64 items-center justify-center">
              <p className="text-muted-foreground">Загрузка...</p>
            </div>
          ) : recipes.length === 0 ? (
            <div className="flex h-64 flex-col items-center justify-center gap-2 rounded-lg border border-dashed">
              <BookOpen className="h-12 w-12 text-muted-foreground" />
              <p className="text-muted-foreground">Рецепты не найдены</p>
              <Button variant="outline" onClick={openCreateDialog}>
                Добавить первый рецепт
              </Button>
            </div>
          ) : (
            <>
              <div className="rounded-lg border">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>ID</TableHead>
                      <TableHead>Название</TableHead>
                      <TableHead>Блюдо (OneToOne)</TableHead>
                      <TableHead>Ингредиенты</TableHead>
                      <TableHead className="text-right">Действия</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {recipes.map((recipe) => {
                      const dishName = getDishName(recipe.dishId);
                      return (
                        <TableRow
                          key={recipe.id}
                          className={
                            selectedRecipe?.id === recipe.id ? "bg-muted/50" : ""
                          }
                        >
                          <TableCell className="font-medium">
                            {recipe.id}
                          </TableCell>
                          <TableCell>{recipe.name}</TableCell>
                          <TableCell>
                            {dishName ? (
                              <span className="rounded-md bg-secondary px-2 py-1 text-sm">
                                {dishName}
                              </span>
                            ) : (
                              <span className="text-muted-foreground">-</span>
                            )}
                          </TableCell>
                          <TableCell>
                            <Button
                              variant="link"
                              className="h-auto p-0 text-primary"
                              onClick={() => viewIngredients(recipe)}
                            >
                              <Layers className="mr-1 h-4 w-4" />
                              Просмотр
                            </Button>
                          </TableCell>
                          <TableCell className="text-right">
                            <Button
                              variant="ghost"
                              size="icon"
                              onClick={() => openEditDialog(recipe)}
                            >
                              <Pencil className="h-4 w-4" />
                            </Button>
                            <Button
                              variant="ghost"
                              size="icon"
                              onClick={() => openDeleteDialog(recipe)}
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

              {/* Pagination */}
              <div className="flex items-center justify-between">
                <p className="text-sm text-muted-foreground">
                  Страница {page + 1} из {totalPages || 1}
                </p>
                <div className="flex gap-2">
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => setPage((p) => Math.max(0, p - 1))}
                    disabled={page === 0}
                  >
                    <ChevronLeft className="h-4 w-4" />
                    Назад
                  </Button>
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => setPage((p) => p + 1)}
                    disabled={page >= totalPages - 1}
                  >
                    Вперед
                    <ChevronRight className="h-4 w-4" />
                  </Button>
                </div>
              </div>
            </>
          )}
        </div>

        {/* Ingredients Panel (OneToMany relationship display) */}
        {selectedRecipe && (
          <div className="w-96 border-l bg-card p-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-semibold">OneToMany: Ингредиенты</h3>
              <Button
                variant="ghost"
                size="icon"
                onClick={closeIngredientsPanel}
              >
                <X className="h-4 w-4" />
              </Button>
            </div>
            <Card>
              <CardHeader>
                <CardTitle className="text-lg">{selectedRecipe.name}</CardTitle>
                <p className="text-sm text-muted-foreground">
                  {selectedRecipe.instructions || "Без инструкций"}
                </p>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="flex items-center justify-between">
                  <h4 className="text-sm font-medium">
                    Ингредиенты ({recipeIngredients.length})
                  </h4>
                  <Button size="sm" onClick={openAddIngredientDialog}>
                    <Plus className="mr-1 h-3 w-3" />
                    Добавить
                  </Button>
                </div>
                {recipeIngredients.length === 0 ? (
                  <p className="text-sm text-muted-foreground">
                    Нет ингредиентов
                  </p>
                ) : (
                  <div className="space-y-2">
                    {recipeIngredients.map((ing) => (
                      <div
                        key={ing.id}
                        className="flex items-center justify-between rounded-lg border p-3"
                      >
                        <div>
                          <p className="font-medium">
                            {getProductName(ing.productId)}
                          </p>
                          <p className="text-sm text-muted-foreground">
                            Количество: {ing.quantity}
                          </p>
                        </div>
                        <Button
                          variant="ghost"
                          size="icon"
                          onClick={() => handleDeleteIngredient(ing.id)}
                        >
                          <Trash2 className="h-4 w-4 text-destructive" />
                        </Button>
                      </div>
                    ))}
                  </div>
                )}
              </CardContent>
            </Card>
          </div>
        )}
      </div>

      {/* Create/Edit Recipe Dialog */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>
              {editingRecipe ? "Редактировать рецепт" : "Новый рецепт"}
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
                  placeholder="Введите название рецепта"
                  required
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="instructions">Инструкции</Label>
                <textarea
                  id="instructions"
                  value={formData.instructions}
                  onChange={(e) =>
                    setFormData({ ...formData, instructions: e.target.value })
                  }
                  placeholder="Введите инструкции по приготовлению"
                  className="flex min-h-24 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="dish">Блюдо (OneToOne связь)</Label>
                <Select
                  value={formData.dishId}
                  onValueChange={(value) =>
                    setFormData({ ...formData, dishId: value })
                  }
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Выберите блюдо (опционально)" />
                  </SelectTrigger>
                  <SelectContent>
                    {dishes.map((dish) => (
                      <SelectItem key={dish.id} value={dish.id.toString()}>
                        {dish.name}
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
                {editingRecipe ? "Сохранить" : "Создать"}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Add Ingredient Dialog */}
      <Dialog open={ingredientDialogOpen} onOpenChange={setIngredientDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Добавить ингредиент</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleAddIngredient}>
            <div className="space-y-4 py-4">
              <div className="space-y-2">
                <Label htmlFor="product">Продукт (ManyToOne)</Label>
                <Select
                  value={ingredientForm.productId}
                  onValueChange={(value) =>
                    setIngredientForm({ ...ingredientForm, productId: value })
                  }
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Выберите продукт" />
                  </SelectTrigger>
                  <SelectContent>
                    {products.map((product) => (
                      <SelectItem key={product.id} value={product.id.toString()}>
                        {product.name}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-2">
                <Label htmlFor="quantity">Количество</Label>
                <Input
                  id="quantity"
                  type="number"
                  step="0.01"
                  value={ingredientForm.quantity}
                  onChange={(e) =>
                    setIngredientForm({
                      ...ingredientForm,
                      quantity: e.target.value,
                    })
                  }
                  placeholder="0"
                  required
                />
              </div>
            </div>
            <DialogFooter>
              <Button
                type="button"
                variant="outline"
                onClick={() => setIngredientDialogOpen(false)}
              >
                Отмена
              </Button>
              <Button type="submit">Добавить</Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <Dialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Удалить рецепт?</DialogTitle>
          </DialogHeader>
          <p className="text-sm text-muted-foreground">
            Вы уверены, что хотите удалить рецепт &quot;{recipeToDelete?.name}&quot;? Это
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
