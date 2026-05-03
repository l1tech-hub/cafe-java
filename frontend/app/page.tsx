"use client";

import { useEffect, useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { PageHeader } from "@/components/layout/page-header";
import {
  UtensilsCrossed,
  Package,
  BookOpen,
  Boxes,
  AlertTriangle,
} from "lucide-react";
import { dishesApi, productsApi, recipesApi, batchesApi } from "@/lib/api";
import type { Dish, Product, Recipe, Batch } from "@/lib/types";

export default function DashboardPage() {
  const [stats, setStats] = useState({
    dishes: 0,
    products: 0,
    recipes: 0,
    batches: 0,
  });
  const [expiringBatches, setExpiringBatches] = useState<Batch[]>([]);
  const [recentDishes, setRecentDishes] = useState<Dish[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadData() {
      try {
        const [dishes, products, recipes, batches] = await Promise.all([
          dishesApi.getAll(),
          productsApi.getAll(),
          recipesApi.getAll(),
          batchesApi.getAll(),
        ]);

        setStats({
          dishes: dishes.length,
          products: products.length,
          recipes: recipes.length,
          batches: batches.length,
        });

        // Find batches expiring within 7 days
        const now = new Date();
        const weekFromNow = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000);
        const expiring = batches.filter((batch) => {
          const expiryDate = new Date(batch.expiryDate);
          return expiryDate <= weekFromNow && expiryDate >= now;
        });
        setExpiringBatches(expiring.slice(0, 5));

        setRecentDishes(dishes.slice(-5).reverse());
      } catch (error) {
        console.error("Failed to load dashboard data:", error);
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, []);

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
        title="Dashboard"
        description="Обзор системы управления кафе"
      />

      <div className="flex-1 space-y-6 p-6">
        {/* Stats Grid */}
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
          {statCards.map((stat) => (
            <Card key={stat.title}>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">
                  {stat.title}
                </CardTitle>
                <stat.icon className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">{stat.value}</div>
              </CardContent>
            </Card>
          ))}
        </div>

        <div className="grid gap-6 md:grid-cols-2">
          {/* Expiring Batches */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2 text-lg">
                <AlertTriangle className="h-5 w-5 text-yellow-500" />
                Истекающие партии
              </CardTitle>
            </CardHeader>
            <CardContent>
              {expiringBatches.length === 0 ? (
                <p className="text-sm text-muted-foreground">
                  Нет партий с истекающим сроком годности
                </p>
              ) : (
                <div className="space-y-3">
                  {expiringBatches.map((batch) => (
                    <div
                      key={batch.id}
                      className="flex items-center justify-between rounded-lg border p-3"
                    >
                      <div>
                        <p className="font-medium">Партия #{batch.id}</p>
                        <p className="text-sm text-muted-foreground">
                          {batch.quantity} шт.
                        </p>
                      </div>
                      <div className="text-right">
                        <p className="text-sm font-medium text-yellow-500">
                          {new Date(batch.expiryDate).toLocaleDateString("ru")}
                        </p>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </CardContent>
          </Card>

          {/* Recent Dishes */}
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Последние блюда</CardTitle>
            </CardHeader>
            <CardContent>
              {recentDishes.length === 0 ? (
                <p className="text-sm text-muted-foreground">Нет блюд</p>
              ) : (
                <div className="space-y-3">
                  {recentDishes.map((dish) => (
                    <div
                      key={dish.id}
                      className="flex items-center justify-between rounded-lg border p-3"
                    >
                      <div>
                        <p className="font-medium">{dish.name}</p>
                        <p className="text-sm text-muted-foreground">
                          {dish.weight} г
                        </p>
                      </div>
                      <div className="text-right">
                        <p className="font-medium">{dish.price} руб.</p>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
