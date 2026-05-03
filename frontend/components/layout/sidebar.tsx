"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import {
  UtensilsCrossed,
  Package,
  BookOpen,
  Calculator,
  LayoutDashboard,
  Coffee,
} from "lucide-react";
import { cn } from "@/lib/utils";

const navigation = [
  { name: "Администрирование", href: "/", icon: LayoutDashboard },
  { name: "Продукты", href: "/products", icon: Package },
  { name: "Рецепты", href: "/recipes", icon: BookOpen },
  { name: "Блюда", href: "/dishes", icon: UtensilsCrossed },
  { name: "Расчёт и приготовление", href: "/cooking", icon: Calculator },
];

export function Sidebar() {
  const pathname = usePathname();

  return (
    <div className="flex h-full w-64 flex-col border-r bg-card">
      <div className="flex h-16 items-center gap-2 border-b px-6">
        <Coffee className="h-6 w-6 text-primary" />
        <span className="text-xl font-bold">Cafe Manager</span>
      </div>
      <nav className="flex-1 space-y-1 p-4">
        {navigation.map((item) => {
          const isActive = pathname === item.href;
          return (
            <Link
              key={item.name}
              href={item.href}
              className={cn(
                "flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium transition-colors",
                isActive
                  ? "bg-primary text-primary-foreground"
                  : "text-muted-foreground hover:bg-muted hover:text-foreground"
              )}
            >
              <item.icon className="h-5 w-5" />
              {item.name}
            </Link>
          );
        })}
      </nav>
      <div className="border-t p-4">
        <p className="text-xs text-muted-foreground">
          Cafe Java Management System
        </p>
      </div>
    </div>
  );
}
