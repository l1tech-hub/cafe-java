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
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Plus, Trash2, Layers, ArrowRight } from "lucide-react";
import { recipesApi, productsApi, ingredientsApi } from "@/lib/api";
import type { Recipe, Product, Ingredient } from "@/lib/types";

interface RecipeWithIngredients {
  recipe: Recipe;
  ingredients: Ingredient[];
}

export default function IngredientsPage() {
  const [recipes, setRecipes] = useState<Recipe[]>([]);
  const [products, setProducts] = useState<Product[]>([]);
  const [recipeIngredients, setRecipeIngredients] = useState<RecipeWithIngredients[]>([]);
  const [loading, setLoading] = useState(true);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [formData, setFormData] = useState({
    recipeId: "",
    productId: "",
    quantity: "",
  });

  useEffect(() => {
    loadData();
  }, []);

  async function loadData() {
    try {
      setLoading(true);
      const [recipesData, productsData] = await Promise.all([
        recipesApi.getAll(),
        productsApi.getAll(),
      ]);
      setRecipes(recipesData);
      setProducts(productsData);

      // Load ingredients for each recipe
      const ingredientsPromises = recipesData.map(async (recipe) => {
        try {
          const ingredients = await ingredientsApi.getByRecipe(recipe.id);
          return { recipe, ingredients };
        } catch {
          return { recipe, ingredients: [] };
        }
      });

      const results = await Promise.all(ingredientsPromises);
      setRecipeIngredients(results.filter((r) => r.ingredients.length > 0));
    } catch (error) {
      console.error("Failed to load data:", error);
    } finally {
      setLoading(false);
    }
  }

  function getProductName(productId: number) {
    return products.find((p) => p.id === productId)?.name ?? `ID: ${productId}`;
  }

  function openCreateDialog() {
    setFormData({ recipeId: "", productId: "", quantity: "" });
    setDialogOpen(true);
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    try {
      await ingredientsApi.create(parseInt(formData.recipeId), {
        productId: parseInt(formData.productId),
        quantity: parseFloat(formData.quantity),
      });
      setDialogOpen(false);
      loadData();
    } catch (error) {
      console.error("Failed to create ingredient:", error);
    }
  }

  async function handleDelete(ingredientId: number) {
    try {
      await ingredientsApi.delete(ingredientId);
      loadData();
    } catch (error) {
      console.error("Failed to delete ingredient:", error);
    }
  }

  // Build ManyToMany visualization data
  const manyToManyData: Record<string, Set<string>> = {};
  recipeIngredients.forEach(({ recipe, ingredients }) => {
    ingredients.forEach((ing) => {
      const productName = getProductName(ing.productId);
      if (!manyToManyData[productName]) {
        manyToManyData[productName] = new Set();
      }
      manyToManyData[productName].add(recipe.name);
    });
  });

  return (
    <div className="flex flex-col">
      <PageHeader
        title="Ингредиенты"
        description="Визуализация ManyToMany связи между рецептами и продуктами"
      >
        <Button onClick={openCreateDialog}>
          <Plus className="mr-2 h-4 w-4" />
          Добавить ингредиент
        </Button>
      </PageHeader>

      <div className="flex-1 space-y-6 p-6">
        {loading ? (
          <div className="flex h-64 items-center justify-center">
            <p className="text-muted-foreground">Загрузка...</p>
          </div>
        ) : (
          <>
            {/* ManyToMany Visualization */}
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Layers className="h-5 w-5" />
                  ManyToMany: Продукты в Рецептах
                </CardTitle>
              </CardHeader>
              <CardContent>
                {Object.keys(manyToManyData).length === 0 ? (
                  <p className="text-muted-foreground">
                    Нет связей между рецептами и продуктами
                  </p>
                ) : (
                  <div className="space-y-4">
                    {Object.entries(manyToManyData).map(
                      ([productName, recipeNames]) => (
                        <div
                          key={productName}
                          className="flex items-center gap-4 rounded-lg border p-4"
                        >
                          <div className="min-w-32 rounded-md bg-primary/10 px-3 py-2 text-center font-medium text-primary">
                            {productName}
                          </div>
                          <ArrowRight className="h-4 w-4 text-muted-foreground" />
                          <div className="flex flex-wrap gap-2">
                            {Array.from(recipeNames).map((recipeName) => (
                              <span
                                key={recipeName}
                                className="rounded-full bg-secondary px-3 py-1 text-sm"
                              >
                                {recipeName}
                              </span>
                            ))}
                          </div>
                        </div>
                      )
                    )}
                  </div>
                )}
              </CardContent>
            </Card>

            {/* Grouped by Recipe */}
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
              {recipeIngredients.map(({ recipe, ingredients }) => (
                <Card key={recipe.id}>
                  <CardHeader>
                    <CardTitle className="text-lg">{recipe.name}</CardTitle>
                    <p className="text-sm text-muted-foreground">
                      {ingredients.length} ингредиент(ов)
                    </p>
                  </CardHeader>
                  <CardContent>
                    <div className="space-y-2">
                      {ingredients.map((ing) => (
                        <div
                          key={ing.id}
                          className="flex items-center justify-between rounded-lg bg-muted/50 px-3 py-2"
                        >
                          <div>
                            <span className="font-medium">
                              {getProductName(ing.productId)}
                            </span>
                            <span className="ml-2 text-sm text-muted-foreground">
                              ({ing.quantity} ед.)
                            </span>
                          </div>
                          <Button
                            variant="ghost"
                            size="icon"
                            onClick={() => handleDelete(ing.id)}
                          >
                            <Trash2 className="h-4 w-4 text-destructive" />
                          </Button>
                        </div>
                      ))}
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>

            {recipeIngredients.length === 0 && (
              <div className="flex h-32 flex-col items-center justify-center gap-2 rounded-lg border border-dashed">
                <Layers className="h-8 w-8 text-muted-foreground" />
                <p className="text-muted-foreground">
                  Нет ингредиентов в рецептах
                </p>
                <Button variant="outline" onClick={openCreateDialog}>
                  Добавить первый ингредиент
                </Button>
              </div>
            )}
          </>
        )}
      </div>

      {/* Create Ingredient Dialog */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Добавить ингредиент</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleSubmit}>
            <div className="space-y-4 py-4">
              <div className="space-y-2">
                <Label htmlFor="recipe">Рецепт</Label>
                <Select
                  value={formData.recipeId}
                  onValueChange={(value) =>
                    setFormData({ ...formData, recipeId: value })
                  }
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Выберите рецепт" />
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
              <div className="space-y-2">
                <Label htmlFor="product">Продукт</Label>
                <Select
                  value={formData.productId}
                  onValueChange={(value) =>
                    setFormData({ ...formData, productId: value })
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
                  value={formData.quantity}
                  onChange={(e) =>
                    setFormData({ ...formData, quantity: e.target.value })
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
                onClick={() => setDialogOpen(false)}
              >
                Отмена
              </Button>
              <Button type="submit">Добавить</Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>
    </div>
  );
}
