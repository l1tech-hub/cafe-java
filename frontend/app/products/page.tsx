"use client";

import { Fragment, useEffect, useState } from "react";
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
  const [batchDialogOpen, setBatchDialogOpen] = useState(false);
  const [batchDeleteDialogOpen, setBatchDeleteDialogOpen] = useState(false);
  const [editingBatch, setEditingBatch] = useState<Batch | null>(null);
  const [batchToDelete, setBatchToDelete] = useState<Batch | null>(null);
  const [batchProductId, setBatchProductId] = useState<number | null>(null);
  const [batchFormData, setBatchFormData] = useState({
    price: "",
    quantity: "",
    manufactureDate: "",
    expiryDate: "",
  });

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
      await loadProductBatches(productId);
    }
    setExpandedProduct(productId);
  }

  async function loadProductBatches(productId: number) {
    try {
      const batches = await batchesApi.getByProduct(productId);
      setProductBatches((prev) => ({ ...prev, [productId]: batches }));
    } catch (error) {
      console.error("Failed to load batches:", error);
    }
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

  function openCreateBatchDialog(productId: number) {
    setEditingBatch(null);
    setBatchProductId(productId);
    setBatchFormData({
      price: "",
      quantity: "",
      manufactureDate: "",
      expiryDate: "",
    });
    setBatchDialogOpen(true);
  }

  function openEditBatchDialog(batch: Batch) {
    setEditingBatch(batch);
    setBatchProductId(batch.productId);
    setBatchFormData({
      price: batch.price.toString(),
      quantity: batch.quantity.toString(),
      manufactureDate: batch.manufactureDate.split("T")[0],
      expiryDate: batch.expiryDate.split("T")[0],
    });
    setBatchDialogOpen(true);
  }

  function openDeleteBatchDialog(batch: Batch) {
    setBatchToDelete(batch);
    setBatchDeleteDialogOpen(true);
  }

  async function handleBatchSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!batchProductId) return;
    try {
      const payload = {
        productId: batchProductId,
        price: parseFloat(batchFormData.price),
        quantity: parseFloat(batchFormData.quantity),
        manufactureDate: batchFormData.manufactureDate,
        expiryDate: batchFormData.expiryDate,
      };

      if (editingBatch) {
        await batchesApi.update(editingBatch.id, payload);
      } else {
        await batchesApi.create(payload);
      }
      setBatchDialogOpen(false);
      await loadProductBatches(batchProductId);
    } catch (error) {
      console.error("Failed to save batch:", error);
    }
  }

  async function handleDeleteBatch() {
    if (!batchToDelete) return;
    try {
      await batchesApi.delete(batchToDelete.id);
      setBatchDeleteDialogOpen(false);
      await loadProductBatches(batchToDelete.productId);
      setBatchToDelete(null);
    } catch (error) {
      console.error("Failed to delete batch:", error);
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
        description="Управление продуктами и их партиями"
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
                  <TableHead>Название</TableHead>
                  <TableHead>Партии</TableHead>
                  <TableHead className="text-right">Действия</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {products.map((product) => (
                  <Fragment key={product.id}>
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
                        <TableCell colSpan={4} className="bg-muted/50 p-4">
                          <div className="space-y-2">
                            <h4 className="font-medium">
                              Партии продукта
                            </h4>
                            <Button size="sm" onClick={() => openCreateBatchDialog(product.id)}>
                              <Plus className="mr-1 h-3 w-3" />
                              Добавить партию
                            </Button>
                            {!productBatches[product.id] ||
                            productBatches[product.id].length === 0 ? (
                              <p className="text-sm text-muted-foreground">
                                Нет партий для этого продукта
                              </p>
                            ) : (
                              <div className="grid gap-2 sm:grid-cols-2 lg:grid-cols-3">
                                {productBatches[product.id].map((batch) => {
                                  const status = getBatchStatus(batch.expiryDate);
                                  const mfgLabel = new Date(
                                    batch.manufactureDate
                                  ).toLocaleDateString("ru");
                                  return (
                                    <div
                                      key={batch.id}
                                      className="rounded-lg border bg-card p-3"
                                    >
                                      <div className="flex items-center justify-between">
                                        <span className="font-medium">
                                          произв. {mfgLabel}
                                        </span>
                                        <div className="flex items-center gap-2">
                                          <span
                                            className={`rounded-full px-2 py-0.5 text-xs ${status.className}`}
                                          >
                                            {status.label}
                                          </span>
                                          <Button
                                            variant="ghost"
                                            size="icon"
                                            onClick={() => openEditBatchDialog(batch)}
                                          >
                                            <Pencil className="h-4 w-4" />
                                          </Button>
                                          <Button
                                            variant="ghost"
                                            size="icon"
                                            onClick={() => openDeleteBatchDialog(batch)}
                                          >
                                            <Trash2 className="h-4 w-4 text-destructive" />
                                          </Button>
                                        </div>
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
                  </Fragment>
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

      <Dialog open={batchDialogOpen} onOpenChange={setBatchDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>
              {editingBatch ? "Редактировать партию" : "Новая партия"}
            </DialogTitle>
          </DialogHeader>
          <form onSubmit={handleBatchSubmit}>
            <div className="space-y-4 py-4">
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="batchQuantity">Количество</Label>
                  <Input
                    id="batchQuantity"
                    type="number"
                    value={batchFormData.quantity}
                    onChange={(e) =>
                      setBatchFormData({ ...batchFormData, quantity: e.target.value })
                    }
                    required
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="batchPrice">Цена</Label>
                  <Input
                    id="batchPrice"
                    type="number"
                    step="0.01"
                    value={batchFormData.price}
                    onChange={(e) =>
                      setBatchFormData({ ...batchFormData, price: e.target.value })
                    }
                    required
                  />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="batchManufactureDate">Дата производства</Label>
                  <Input
                    id="batchManufactureDate"
                    type="date"
                    value={batchFormData.manufactureDate}
                    onChange={(e) =>
                      setBatchFormData({
                        ...batchFormData,
                        manufactureDate: e.target.value,
                      })
                    }
                    required
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="batchExpiryDate">Срок годности</Label>
                  <Input
                    id="batchExpiryDate"
                    type="date"
                    value={batchFormData.expiryDate}
                    onChange={(e) =>
                      setBatchFormData({ ...batchFormData, expiryDate: e.target.value })
                    }
                    required
                  />
                </div>
              </div>
            </div>
            <DialogFooter>
              <Button type="button" variant="outline" onClick={() => setBatchDialogOpen(false)}>
                Отмена
              </Button>
              <Button type="submit">{editingBatch ? "Сохранить" : "Создать"}</Button>
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

      <Dialog open={batchDeleteDialogOpen} onOpenChange={setBatchDeleteDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Удалить партию?</DialogTitle>
          </DialogHeader>
          <p className="text-sm text-muted-foreground">
            Вы уверены, что хотите удалить партию #{batchToDelete?.id}? Это действие
            нельзя отменить.
          </p>
          <DialogFooter>
            <Button variant="outline" onClick={() => setBatchDeleteDialogOpen(false)}>
              Отмена
            </Button>
            <Button variant="destructive" onClick={handleDeleteBatch}>
              Удалить
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
