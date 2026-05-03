import type {
  Dish,
  Product,
  Recipe,
  Ingredient,
  Batch,
  CreateDishRequest,
  CreateProductRequest,
  CreateRecipeRequest,
  CreateIngredientRequest,
  CreateBatchRequest,
  DishCookStat,
  DishCookTaskStatus,
  ProductSpent,
  IngredientMissing,
  BatchOrder,
  RecipeCostEstimate,
  Page,
} from "./types";

const API_BASE = "/api";

async function fetchApi<T>(
  endpoint: string,
  options?: RequestInit
): Promise<T> {
  const response = await fetch(`${API_BASE}${endpoint}`, {
    headers: {
      "Content-Type": "application/json",
      ...options?.headers,
    },
    ...options,
  });

  if (!response.ok) {
    const error = await response.text();
    throw new Error(error || `HTTP ${response.status}`);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  const contentType = response.headers.get("content-type") ?? "";
  if (contentType.includes("application/json")) {
    return response.json();
  }

  const text = await response.text();
  return text as T;
}

// Dishes API
export const dishesApi = {
  getAll: () => fetchApi<Dish[]>("/dishes"),
  getById: (id: number) => fetchApi<Dish>(`/dishes/${id}`),
  create: (data: CreateDishRequest) =>
    fetchApi<Dish>("/dishes", {
      method: "POST",
      body: JSON.stringify(data),
    }),
  update: (id: number, data: CreateDishRequest) =>
    fetchApi<Dish>(`/dishes/${id}`, {
      method: "PUT",
      body: JSON.stringify(data),
    }),
  delete: (id: number) =>
    fetchApi<void>(`/dishes/${id}`, { method: "DELETE" }),
  search: (name: string) =>
    fetchApi<Dish[]>(`/dishes/search?name=${encodeURIComponent(name)}`),
  cook: (id: number, allowExpiredProducts: boolean, batchOrder: BatchOrder) =>
    fetchApi<string>(
      `/dishes/${id}/cook?allowExpiredProducts=${allowExpiredProducts}&batchOrder=${batchOrder}`,
      {
        method: "POST",
      }
    ),
  getCookTaskStatus: (taskId: string) =>
    fetchApi<DishCookTaskStatus>(`/dishes/tasks/${taskId}`),
  getCookingStatistics: () =>
    fetchApi<DishCookStat[]>("/dishes/cooking-statistics"),
  getSpentProductsKilograms: () =>
    fetchApi<ProductSpent[]>("/dishes/spent-products"),
};

// Products API
export const productsApi = {
  getAll: () => fetchApi<Product[]>("/products"),
  getById: (id: number) => fetchApi<Product>(`/products/${id}`),
  create: (data: CreateProductRequest) =>
    fetchApi<Product>("/products", {
      method: "POST",
      body: JSON.stringify(data),
    }),
  update: (id: number, data: CreateProductRequest) =>
    fetchApi<Product>(`/products/${id}`, {
      method: "PUT",
      body: JSON.stringify(data),
    }),
  delete: (id: number) =>
    fetchApi<void>(`/products/${id}`, { method: "DELETE" }),
  search: (name: string) =>
    fetchApi<Product[]>(`/products/search?name=${encodeURIComponent(name)}`),
};

// Recipes API
export const recipesApi = {
  getAll: () => fetchApi<Recipe[]>("/recipes"),
  getById: (id: number) => fetchApi<Recipe>(`/recipes/${id}`),
  create: (data: CreateRecipeRequest) =>
    fetchApi<Recipe>("/recipes", {
      method: "POST",
      body: JSON.stringify(data),
    }),
  update: (id: number, data: CreateRecipeRequest) =>
    fetchApi<Recipe>(`/recipes/${id}`, {
      method: "PUT",
      body: JSON.stringify(data),
    }),
  delete: (id: number) =>
    fetchApi<void>(`/recipes/${id}`, { method: "DELETE" }),
  getPaged: (page: number, size: number, dishId?: number) => {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
    });
    if (dishId) params.append("dishId", dishId.toString());
    return fetchApi<Page<Recipe>>(`/recipes/paged?${params}`);
  },
  getCostEstimate: (recipeId: number, itr: number, date: string, batchOrder: BatchOrder) =>
    fetchApi<RecipeCostEstimate>(
      `/recipes/${recipeId}/cost-estimate?itr=${encodeURIComponent(
        String(itr)
      )}&date=${encodeURIComponent(date)}&batchOrder=${batchOrder}`
    ),
};

// Ingredients API
export const ingredientsApi = {
  getByRecipe: (recipeId: number) =>
    fetchApi<Ingredient[]>(`/ingredients/recipe/${recipeId}`),
  getMissing: (
    recipeId: number,
    iterations: number,
    date: string,
    allowExpiredProducts?: boolean
  ) => {
    const params = new URLSearchParams({
      itr: String(iterations),
      date,
    });
    if (allowExpiredProducts) params.set("allowExpired", "true");
    return fetchApi<IngredientMissing[]>(
      `/ingredients/recipe/${recipeId}/missing?${params.toString()}`
    );
  },
  create: (recipeId: number, data: CreateIngredientRequest) =>
    fetchApi<Ingredient>(`/ingredients?recipeId=${recipeId}`, {
      method: "POST",
      body: JSON.stringify(data),
    }),
  delete: (id: number) =>
    fetchApi<void>(`/ingredients/${id}`, { method: "DELETE" }),
};

// Batches API
export const batchesApi = {
  getAll: () => fetchApi<Batch[]>("/batches"),
  getExpired: (asOfDate?: string) => {
    const q = asOfDate
      ? `?asOf=${encodeURIComponent(asOfDate)}`
      : "";
    return fetchApi<Batch[]>(`/batches/expired${q}`);
  },
  getById: (id: number) => fetchApi<Batch>(`/batches/${id}`),
  create: (data: CreateBatchRequest) =>
    fetchApi<Batch>("/batches", {
      method: "POST",
      body: JSON.stringify(data),
    }),
  update: (id: number, data: CreateBatchRequest) =>
    fetchApi<Batch>(`/batches/${id}`, {
      method: "PUT",
      body: JSON.stringify(data),
    }),
  delete: (id: number) =>
    fetchApi<void>(`/batches/${id}`, { method: "DELETE" }),
  getByProduct: (productId: number) =>
    fetchApi<Batch[]>(`/batches/product/${productId}`),
};
