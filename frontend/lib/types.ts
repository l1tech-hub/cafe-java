// Types based on Java DTOs

export interface Dish {
  id: number;
  name: string;
  price: number;
  weight: number;
  recipeId: number | null;
}

export interface Product {
  id: number;
  name: string;
}

export interface Recipe {
  id: number;
  name: string;
  instructions: string;
  dishId: number | null;
  ingredients: Ingredient[];
}

export interface Ingredient {
  id: number;
  recipeId: number;
  productId: number;
  productName?: string;
  quantity: number;
}

export interface Batch {
  id: number;
  productId: number;
  productName?: string;
  price: number;
  quantity: number;
  manufactureDate: string;
  expiryDate: string;
}

// Request DTOs
export interface CreateDishRequest {
  name: string;
  price: number;
  weight: number;
  recipeId?: number;
}

export interface CreateProductRequest {
  name: string;
}

export interface CreateRecipeRequest {
  name: string;
  instructions: string;
  dishId?: number;
  ingredients: CreateIngredientRequest[];
}

export interface CreateIngredientRequest {
  productId: number;
  quantity: number;
}

export interface CreateBatchRequest {
  productId: number;
  price: number;
  quantity: number;
  manufactureDate: string;
  expiryDate: string;
}

// Pagination
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
