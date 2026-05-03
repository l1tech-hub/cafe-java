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
import { Plus, Pencil, Trash2, Boxes, Filter } from "lucide-react";
import { batchesApi, productsApi } from "@/lib/api";
import type { Batch, Product } from "@/lib/types";

export default function BatchesPage() {
  const [batches, setBatches] = useState<Batch[]>([]);
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [filterProductId, setFilterProductId] = useState<string>("");
  const [dialogOpen, setDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [editingBatch, setEditingBatch] = useState<Batch | null>(null);
  const [batchToDelete, setBatchToDelete] = useState<Batch | null>(null);
  const [formData, setFormData] = useState({
    productId: "",
    price: "",
    quantity: "",
    manufactureDate: "",
    expiryDate: "",
  });

  useEffect(() => {
    loadData();
  }, []);

  async function loadData() {
    try {
      setLoading(true);
      const [batchesData, productsData] = await Promise.all([
        batchesApi.getAll(),
        productsApi.getAll(),
      ]);
      setBatches(batchesData);
      setProducts(productsData);
    } catch (error) {
      console.error("Failed to load data:", error);
    } finally {
      setLoading(false);
    }
  }

  async function handleFilter() {
    if (!filterProductId) {
      loadData();
      return;
    }
    try {
      setLoading(true);
      const data = await batchesApi.getByProduct(parseInt(filterProductId));
      setBatches(data);
    } catch (error) {
      console.error("Failed to filter batches:", error);
    } finally {
      setLoading(false);
    }
  }

  function getProductName(productId: number) {
    return products.find((p) => p.id === productId)?.name ?? `ID: ${productId}`;
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

  function openCreateDialog() {
    setEditingBatch(null);
    setFormData({
      productId: "",
      price: "",
      quantity: "",
      manufactureDate: "",
      expiryDate: "",
    });
    setDialogOpen(true);
  }

  function openEditDialog(batch: Batch) {
    setEditingBatch(batch);
    setFormData({
      productId: batch.productId.toString(),
      price: batch.price.toString(),
      quantity: batch.quantity.toString(),
      manufactureDate: batch.manufactureDate.split("T")[0],
      expiryDate: batch.expiryDate.split("T")[0],
    });
    setDialogOpen(true);
  }

  function openDeleteDialog(batch: Batch) {
    setBatchToDelete(batch);
    setDeleteDialogOpen(true);
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    try {
      const data = {
        productId: parseInt(formData.productId),
        price: parseFloat(formData.price),
        quantity: parseFloat(formData.quantity),
        manufactureDate: formData.manufactureDate,
        expiryDate: formData.expiryDate,
      };
      if (editingBatch) {
        await batchesApi.update(editingBatch.id, data);
      } else {
        await batchesApi.create(data);
      }
      setDialogOpen(false);
      loadData();
    } catch (error) {
      console.error("Failed to save batch:", error);
    }
  }

  async function handleDelete() {
    if (!batchToDelete) return;
    try {
      await batchesApi.delete(batchToDelete.id);
      setDeleteDialogOpen(false);
      setBatchToDelete(null);
      loadData();
    } catch (error) {
      console.error("Failed to delete batch:", error);
    }
  }

  return (
    <div className="flex flex-col">
      <PageHeader
        title="Партии"
        description="Управление партиями продуктов"
      >
        <Button onClick={openCreateDialog}>
          <Plus className="mr-2 h-4 w-4" />
          Добавить партию
        </Button>
      </PageHeader>

      <div className="flex-1 space-y-4 p-6">
        {/* Filter by Product */}
        <div className="flex items-center gap-2">
          <Filter className="h-4 w-4 text-muted-foreground" />
          <Select value={filterProductId} onValueChange={setFilterProductId}>
            <SelectTrigger className="w-64">
              <SelectValue placeholder="Фильтр по продукту" />
            </SelectTrigger>
            <SelectContent>
              {products.map((product) => (
                <SelectItem key={product.id} value={product.id.toString()}>
                  {product.name}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
          <Button variant="secondary" onClick={handleFilter}>
            Применить
          </Button>
          <Button
            variant="outline"
            onClick={() => {
              setFilterProductId("");
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
        ) : batches.length === 0 ? (
          <div className="flex h-64 flex-col items-center justify-center gap-2 rounded-lg border border-dashed">
            <Boxes className="h-12 w-12 text-muted-foreground" />
            <p className="text-muted-foreground">Партии не найдены</p>
            <Button variant="outline" onClick={openCreateDialog}>
              Добавить первую партию
            </Button>
          </div>
        ) : (
          <div className="rounded-lg border">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>ID</TableHead>
                  <TableHead>Продукт</TableHead>
                  <TableHead>Количество</TableHead>
                  <TableHead>Цена</TableHead>
                  <TableHead>Дата производства</TableHead>
                  <TableHead>Срок годности</TableHead>
                  <TableHead>Статус</TableHead>
                  <TableHead className="text-right">Действия</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {batches.map((batch) => {
                  const status = getBatchStatus(batch.expiryDate);
                  return (
                    <TableRow key={batch.id}>
                      <TableCell className="font-medium">{batch.id}</TableCell>
                      <TableCell>
                        <span className="rounded-md bg-primary/10 px-2 py-1 text-sm font-medium text-primary">
                          {getProductName(batch.productId)}
                        </span>
                      </TableCell>
                      <TableCell>{batch.quantity}</TableCell>
                      <TableCell>{batch.price} руб.</TableCell>
                      <TableCell>
                        {new Date(batch.manufactureDate).toLocaleDateString("ru")}
                      </TableCell>
                      <TableCell>
                        {new Date(batch.expiryDate).toLocaleDateString("ru")}
                      </TableCell>
                      <TableCell>
                        <span className={`rounded-full px-2 py-1 text-xs ${status.className}`}>
                          {status.label}
                        </span>
                      </TableCell>
                      <TableCell className="text-right">
                        <Button
                          variant="ghost"
                          size="icon"
                          onClick={() => openEditDialog(batch)}
                        >
                          <Pencil className="h-4 w-4" />
                        </Button>
                        <Button
                          variant="ghost"
                          size="icon"
                          onClick={() => openDeleteDialog(batch)}
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

      {/* Create/Edit Dialog */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>
              {editingBatch ? "Редактировать партию" : "Новая партия"}
            </DialogTitle>
          </DialogHeader>
          <form onSubmit={handleSubmit}>
            <div className="space-y-4 py-4">
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
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="quantity">Количество</Label>
                  <Input
                    id="quantity"
                    type="number"
                    value={formData.quantity}
                    onChange={(e) =>
                      setFormData({ ...formData, quantity: e.target.value })
                    }
                    placeholder="0"
                    required
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="price">Цена</Label>
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
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="manufactureDate">Дата производства</Label>
                  <Input
                    id="manufactureDate"
                    type="date"
                    value={formData.manufactureDate}
                    onChange={(e) =>
                      setFormData({ ...formData, manufactureDate: e.target.value })
                    }
                    required
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="expiryDate">Срок годности</Label>
                  <Input
                    id="expiryDate"
                    type="date"
                    value={formData.expiryDate}
                    onChange={(e) =>
                      setFormData({ ...formData, expiryDate: e.target.value })
                    }
                    required
                  />
                </div>
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
                {editingBatch ? "Сохранить" : "Создать"}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <Dialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Удалить партию?</DialogTitle>
          </DialogHeader>
          <p className="text-sm text-muted-foreground">
            Вы уверены, что хотите удалить партию #{batchToDelete?.id}? Это
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
