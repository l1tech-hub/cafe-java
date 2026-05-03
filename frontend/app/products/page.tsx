"use client";

import { useEffect, useState } from "react";
import { PageHeader } from "@/components/layout/page-header";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
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
import { Plus, Pencil, Trash2, Search, ChevronDown, ChevronRight, Package } from "lucide-react";
import { productsApi, batchesApi } from "@/lib/api";
import type { Product, Batch } from "@/lib/types";

export default function ProductsPage() {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");
  const [dialogOpen, setDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [editingProduct, setEditingProduct] = useState<Product | null>(null);
  const [productToDelete, setProductToDelete] = useState<Product | null>(null);
  const [formData, setFormData] = useState({ name: "" });
  const [expandedProduct, setExpandedProduct] = useState<number | null>(null);
  const [productBatches, setProductBatches] = useState<Record<number, Batch[]>>({});

  useEffect(() => {
    loadProducts();
  }, []);

  async function loadProducts() {
    try {
      setLoading(true);
      const data = await productsApi.getAll();
      setProducts(data);
    } catch (error) {
      console.error("Failed to load products:", error);
    } finally {
      setLoading(false);
    }
  }

  async function handleSearch() {
    if (!searchQuery.trim()) {
      loadProducts();
      return;
    }
    try {
      setLoading(true);
      const data = await productsApi.search(searchQuery);
      setProducts(data);
    } catch (error) {
      console.error("Failed to search products:", error);
    } finally {
      setLoading(false);
    }
  }

  async function toggleBatches(productId: number) {
    if (expandedProduct === productId) {
      setExpandedProduct(null);
      return;
    }

    if (!productBatches[productId]) {
      try {
        const batches = await batchesApi.getByProduct(productId);
        setProductBatches((prev) => ({ ...prev, [productId]: batches }));
      } catch (error) {
        console.error("Failed to load batches:", error);
      }
    }
    setExpandedProduct(productId);
  }

  function openCreateDialog() {
    setEditingProduct(null);
    setFormData({ name: "" });
    setDialogOpen(true);
  }

  function openEditDialog(product: Product) {
    setEditingProduct(product);
    setFormData({ name: product.name });
    setDialogOpen(true);
  }

  function openDeleteDialog(product: Product) {
    setProductToDelete(product);
    setDeleteDialogOpen(true);
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    try {
      if (editingProduct) {
        await productsApi.update(editingProduct.id, formData);
      } else {
        await productsApi.create(formData);
      }
      setDialogOpen(false);
      loadProducts();
    } catch (error) {
      console.error("Failed to save product:", error);
    }
  }

  async function handleDelete() {
    if (!productToDelete) return;
    try {
      await productsApi.delete(productToDelete.id);
      setDeleteDialogOpen(false);
      setProductToDelete(null);
      loadProducts();
    } catch (error) {
      console.error("Failed to delete product:", error);
    }
  }

  function getBatchStatus(expiryDate: string) {
    const now = new Date();
    const expiry = new Date(expiryDate);
    const daysUntilExpiry = Math.ceil(
      (expiry.getTime() - now.getTime()) / (1000 * 60 * 60 * 24)
    );

    if (daysUntilExpiry < 0) {
      return { label: "Просрочено", className: "bg-red-500/20 text-red-400" };
    }
    if (daysUntilExpiry <= 7) {
      return { label: "Скоро истекает", className: "bg-yellow-500/20 text-yellow-400" };
    }
    return { label: "В норме", className: "bg-green-500/20 text-green-400" };
  }

  return (
    <div className="flex flex-col">
      <PageHeader
        title="Продукты"
        description="Управление продуктами и их партиями (OneToMany)"
      >
        <Button onClick={openCreateDialog}>
          <Plus className="mr-2 h-4 w-4" />
          Добавить продукт
        </Button>
      </PageHeader>

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
              loadProducts();
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
        ) : products.length === 0 ? (
          <div className="flex h-64 flex-col items-center justify-center gap-2 rounded-lg border border-dashed">
            <Package className="h-12 w-12 text-muted-foreground" />
            <p className="text-muted-foreground">Продукты не найдены</p>
            <Button variant="outline" onClick={openCreateDialog}>
              Добавить первый продукт
            </Button>
          </div>
        ) : (
          <div className="rounded-lg border">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead className="w-12"></TableHead>
                  <TableHead>ID</TableHead>
                  <TableHead>Название</TableHead>
                  <TableHead>Партии</TableHead>
                  <TableHead className="text-right">Действия</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {products.map((product) => (
                  <>
                    <TableRow key={product.id}>
                      <TableCell>
                        <Button
                          variant="ghost"
                          size="icon"
                          onClick={() => toggleBatches(product.id)}
                        >
                          {expandedProduct === product.id ? (
                            <ChevronDown className="h-4 w-4" />
                          ) : (
                            <ChevronRight className="h-4 w-4" />
                          )}
                        </Button>
                      </TableCell>
                      <TableCell className="font-medium">{product.id}</TableCell>
                      <TableCell>{product.name}</TableCell>
                      <TableCell>
                        <span className="rounded-full bg-muted px-2 py-1 text-xs">
                          {productBatches[product.id]?.length ?? "..."}
                        </span>
                      </TableCell>
                      <TableCell className="text-right">
                        <Button
                          variant="ghost"
                          size="icon"
                          onClick={() => openEditDialog(product)}
                        >
                          <Pencil className="h-4 w-4" />
                        </Button>
                        <Button
                          variant="ghost"
                          size="icon"
                          onClick={() => openDeleteDialog(product)}
                        >
                          <Trash2 className="h-4 w-4 text-destructive" />
                        </Button>
                      </TableCell>
                    </TableRow>
                    {expandedProduct === product.id && (
                      <TableRow>
                        <TableCell colSpan={5} className="bg-muted/50 p-4">
                          <div className="space-y-2">
                            <h4 className="font-medium">
                              Партии продукта (OneToMany связь)
                            </h4>
                            {!productBatches[product.id] ||
                            productBatches[product.id].length === 0 ? (
                              <p className="text-sm text-muted-foreground">
                                Нет партий для этого продукта
                              </p>
                            ) : (
                              <div className="grid gap-2 sm:grid-cols-2 lg:grid-cols-3">
                                {productBatches[product.id].map((batch) => {
                                  const status = getBatchStatus(batch.expiryDate);
                                  return (
                                    <div
                                      key={batch.id}
                                      className="rounded-lg border bg-card p-3"
                                    >
                                      <div className="flex items-center justify-between">
                                        <span className="font-medium">
                                          Партия #{batch.id}
                                        </span>
                                        <span
                                          className={`rounded-full px-2 py-0.5 text-xs ${status.className}`}
                                        >
                                          {status.label}
                                        </span>
                                      </div>
                                      <div className="mt-2 grid grid-cols-2 gap-1 text-sm text-muted-foreground">
                                        <span>Количество:</span>
                                        <span>{batch.quantity}</span>
                                        <span>Цена:</span>
                                        <span>{batch.price} руб.</span>
                                        <span>Годен до:</span>
                                        <span>
                                          {new Date(
                                            batch.expiryDate
                                          ).toLocaleDateString("ru")}
                                        </span>
                                      </div>
                                    </div>
                                  );
                                })}
                              </div>
                            )}
                          </div>
                        </TableCell>
                      </TableRow>
                    )}
                  </>
                ))}
              </TableBody>
            </Table>
          </div>
        )}
      </div>

      {/* Create/Edit Dialog */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>
              {editingProduct ? "Редактировать продукт" : "Новый продукт"}
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
                  placeholder="Введите название продукта"
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
              <Button type="submit">
                {editingProduct ? "Сохранить" : "Создать"}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <Dialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Удалить продукт?</DialogTitle>
          </DialogHeader>
          <p className="text-sm text-muted-foreground">
            Вы уверены, что хотите удалить продукт &quot;{productToDelete?.name}&quot;?
            Это действие нельзя отменить.
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
