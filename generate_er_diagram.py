"""
Генератор ER-диаграммы для БД ООО «СтройМатериалы».
Создаёт er_diagram.pdf.
"""

import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.patches import FancyArrowPatch
import matplotlib.patheffects as pe

# ──────────────────────────────────────────────────────────────
# Описание таблиц: {имя: [(колонка, тип, флаги)]}
# флаги: PK / FK / -
# ──────────────────────────────────────────────────────────────
TABLES = {
    "roles": [
        ("id",   "SERIAL",      "PK"),
        ("name", "VARCHAR(50)", "-"),
    ],
    "users": [
        ("id",        "SERIAL",       "PK"),
        ("role_id",   "INT",          "FK → roles"),
        ("full_name", "VARCHAR(150)", "-"),
        ("login",     "VARCHAR(100)", "-"),
        ("password",  "VARCHAR(255)", "-"),
    ],
    "order_statuses": [
        ("id",   "SERIAL",      "PK"),
        ("name", "VARCHAR(50)", "-"),
    ],
    "categories": [
        ("id",   "SERIAL",       "PK"),
        ("name", "VARCHAR(100)", "-"),
    ],
    "suppliers": [
        ("id",   "SERIAL",       "PK"),
        ("name", "VARCHAR(100)", "-"),
    ],
    "manufacturers": [
        ("id",   "SERIAL",       "PK"),
        ("name", "VARCHAR(100)", "-"),
    ],
    "units": [
        ("id",   "SERIAL",      "PK"),
        ("name", "VARCHAR(20)", "-"),
    ],
    "pickup_points": [
        ("id",      "SERIAL",       "PK"),
        ("address", "VARCHAR(255)", "-"),
    ],
    "products": [
        ("id",              "SERIAL",        "PK"),
        ("article",         "VARCHAR(20)",   "-"),
        ("name",            "VARCHAR(255)",  "-"),
        ("unit_id",         "INT",           "FK → units"),
        ("price",           "NUMERIC(12,2)", "-"),
        ("supplier_id",     "INT",           "FK → suppliers"),
        ("manufacturer_id", "INT",           "FK → manufacturers"),
        ("category_id",     "INT",           "FK → categories"),
        ("discount",        "INT",           "-"),
        ("stock_quantity",  "INT",           "-"),
        ("description",     "TEXT",          "-"),
        ("photo",           "VARCHAR(255)",  "-"),
    ],
    "orders": [
        ("id",              "SERIAL", "PK"),
        ("order_number",    "INT",    "-"),
        ("order_date",      "DATE",   "-"),
        ("delivery_date",   "DATE",   "-"),
        ("pickup_point_id", "INT",    "FK → pickup_points"),
        ("user_id",         "INT",    "FK → users"),
        ("pickup_code",     "INT",    "-"),
        ("status_id",       "INT",    "FK → order_statuses"),
    ],
    "order_items": [
        ("id",       "SERIAL",      "PK"),
        ("order_id", "INT",         "FK → orders"),
        ("article",  "VARCHAR(20)", "FK → products"),
        ("quantity", "INT",         "-"),
    ],
}

# ──────────────────────────────────────────────────────────────
# Позиции таблиц (x, y) в координатах «ячеек»
# Одна ячейка ≈ 3 единицы
# ──────────────────────────────────────────────────────────────
POSITIONS = {
    "roles":          (0,   19),
    "users":          (0,   11),
    "order_statuses": (0,    3),
    "categories":     (10,  19),
    "suppliers":      (10,  14),
    "manufacturers":  (10,   9),
    "units":          (10,   4),
    "pickup_points":  (20,  19),
    "products":       (20,   6),
    "orders":         (30,  14),
    "order_items":    (30,   4),
}

TABLE_W = 8.0   # ширина прямоугольника таблицы
ROW_H   = 0.55  # высота одной строки
HEADER_H = 0.65

COLORS = {
    "header_ref":  "#4A90D9",   # справочники
    "header_main": "#2E7D32",   # основные сущности
    "header_link": "#8E24AA",   # связующие таблицы
    "pk_bg":       "#FFF9C4",
    "fk_bg":       "#E3F2FD",
    "row_bg":      "#FFFFFF",
    "border":      "#555555",
}

HEADER_MAP = {
    "roles":          COLORS["header_ref"],
    "order_statuses": COLORS["header_ref"],
    "categories":     COLORS["header_ref"],
    "suppliers":      COLORS["header_ref"],
    "manufacturers":  COLORS["header_ref"],
    "units":          COLORS["header_ref"],
    "users":          COLORS["header_main"],
    "pickup_points":  COLORS["header_main"],
    "products":       COLORS["header_main"],
    "orders":         COLORS["header_main"],
    "order_items":    COLORS["header_link"],
}


def table_height(tname):
    return HEADER_H + ROW_H * len(TABLES[tname])


def draw_table(ax, tname, x, y):
    cols = TABLES[tname]
    h = table_height(tname)

    # Тень
    shadow = mpatches.FancyBboxPatch(
        (x + 0.08, y - h - 0.08), TABLE_W, h,
        boxstyle="round,pad=0.05",
        linewidth=0, facecolor="#cccccc", zorder=1
    )
    ax.add_patch(shadow)

    # Фон всей таблицы
    bg = mpatches.FancyBboxPatch(
        (x, y - h), TABLE_W, h,
        boxstyle="round,pad=0.05",
        linewidth=1.2, edgecolor=COLORS["border"],
        facecolor=COLORS["row_bg"], zorder=2
    )
    ax.add_patch(bg)

    # Заголовок
    hdr = mpatches.FancyBboxPatch(
        (x, y - HEADER_H), TABLE_W, HEADER_H,
        boxstyle="round,pad=0.05",
        linewidth=0, facecolor=HEADER_MAP[tname], zorder=3
    )
    ax.add_patch(hdr)
    ax.text(x + TABLE_W / 2, y - HEADER_H / 2, tname,
            ha="center", va="center",
            fontsize=8, fontweight="bold", color="white", zorder=4)

    # Строки
    for i, (col, dtype, flag) in enumerate(cols):
        ry = y - HEADER_H - (i + 1) * ROW_H
        # Подсветка PK / FK
        if flag == "PK":
            rbg = COLORS["pk_bg"]
        elif flag.startswith("FK"):
            rbg = COLORS["fk_bg"]
        else:
            rbg = COLORS["row_bg"]

        row_rect = mpatches.Rectangle(
            (x, ry), TABLE_W, ROW_H,
            linewidth=0.4, edgecolor="#aaaaaa",
            facecolor=rbg, zorder=2
        )
        ax.add_patch(row_rect)

        # Метка PK / FK
        if flag == "PK":
            ax.text(x + 0.18, ry + ROW_H / 2, "PK",
                    ha="left", va="center",
                    fontsize=5.5, color="#b8860b",
                    fontweight="bold", zorder=5)
        elif flag.startswith("FK"):
            ax.text(x + 0.18, ry + ROW_H / 2, "FK",
                    ha="left", va="center",
                    fontsize=5.5, color="#1565C0",
                    fontweight="bold", zorder=5)

        ax.text(x + 0.65, ry + ROW_H / 2, col,
                ha="left", va="center",
                fontsize=6.5, color="#212121", zorder=5)
        ax.text(x + TABLE_W - 0.15, ry + ROW_H / 2, dtype,
                ha="right", va="center",
                fontsize=5.5, color="#555555", style="italic", zorder=5)


def anchor(tname, side="right"):
    """Возвращает координату якоря на середине стороны таблицы."""
    x, y = POSITIONS[tname]
    h = table_height(tname)
    mid_y = y - h / 2
    if side == "right":
        return (x + TABLE_W, mid_y)
    if side == "left":
        return (x, mid_y)
    if side == "top":
        return (x + TABLE_W / 2, y)
    if side == "bottom":
        return (x + TABLE_W / 2, y - h)
    return (x + TABLE_W / 2, mid_y)


def draw_arrow(ax, p1, p2, color="#888888"):
    ax.annotate("",
                xy=p2, xytext=p1,
                arrowprops=dict(
                    arrowstyle="-|>",
                    color=color,
                    lw=1.1,
                    connectionstyle="arc3,rad=0.0",
                ),
                zorder=1)


# ──────────────────────────────────────────────────────────────
# Связи FK: (таблица-источник, таблица-цель, сторона-src, сторона-dst)
# ──────────────────────────────────────────────────────────────
RELATIONS = [
    ("users",       "roles",          "left",   "right"),
    ("users",       "roles",          "left",   "bottom"),   # дублируем для наглядности — убираем
    ("products",    "categories",     "left",   "right"),
    ("products",    "suppliers",      "left",   "right"),
    ("products",    "manufacturers",  "left",   "right"),
    ("products",    "units",          "left",   "right"),
    ("orders",      "pickup_points",  "left",   "right"),
    ("orders",      "users",          "left",   "right"),
    ("orders",      "order_statuses", "left",   "right"),
    ("order_items", "orders",         "top",    "bottom"),
    ("order_items", "products",       "left",   "right"),
]

# Убираем дубль
RELATIONS = [r for r in RELATIONS if not (r[0] == "users" and r[2] == "left" and r[3] == "bottom")]


def main():
    fig_w, fig_h = 46, 28
    fig, ax = plt.subplots(figsize=(fig_w, fig_h))
    ax.set_xlim(-1, 41)
    ax.set_ylim(-2, 24)
    ax.set_aspect("equal")
    ax.axis("off")
    ax.set_facecolor("#F5F5F5")
    fig.patch.set_facecolor("#F5F5F5")

    # Заголовок
    ax.text(20, 23.3,
            "ER-диаграмма базы данных ООО «СтройМатериалы»",
            ha="center", va="center",
            fontsize=16, fontweight="bold", color="#1A237E")

    # Легенда
    legend_x, legend_y = 33, 23.2
    ax.add_patch(mpatches.Rectangle((legend_x, legend_y - 0.4), 0.5, 0.35,
                                    facecolor=COLORS["header_ref"], edgecolor="none"))
    ax.text(legend_x + 0.65, legend_y - 0.22, "Справочник", va="center", fontsize=7)

    ax.add_patch(mpatches.Rectangle((legend_x + 3, legend_y - 0.4), 0.5, 0.35,
                                    facecolor=COLORS["header_main"], edgecolor="none"))
    ax.text(legend_x + 3.65, legend_y - 0.22, "Основная сущность", va="center", fontsize=7)

    ax.add_patch(mpatches.Rectangle((legend_x + 7.5, legend_y - 0.4), 0.5, 0.35,
                                    facecolor=COLORS["header_link"], edgecolor="none"))
    ax.text(legend_x + 8.15, legend_y - 0.22, "Связующая таблица", va="center", fontsize=7)

    # Связи (под таблицами)
    for src, dst, s_side, d_side in RELATIONS:
        p1 = anchor(src, s_side)
        p2 = anchor(dst, d_side)
        draw_arrow(ax, p1, p2, color="#666699")

    # Таблицы
    for tname, (x, y) in POSITIONS.items():
        draw_table(ax, tname, x, y)

    plt.tight_layout(pad=0.5)
    out = "er_diagram.pdf"
    plt.savefig(out, format="pdf", bbox_inches="tight", dpi=150)
    print(f"ER-диаграмма сохранена: {out}")


if __name__ == "__main__":
    main()
