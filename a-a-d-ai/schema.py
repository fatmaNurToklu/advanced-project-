DB_SCHEMA = """
PostgreSQL database schema for an e-commerce platform called DataPulse.
ALL ID columns are VARCHAR (NOT UUID) — never use ::uuid cast.

TABLES:

users (user_id VARCHAR PK, email VARCHAR, first_name VARCHAR, last_name VARCHAR,
       role VARCHAR CHECK IN ('CUSTOMER','CORPORATE','ADMIN'), gender VARCHAR,
       phone VARCHAR, status BOOLEAN, created_at TIMESTAMP, updated_at TIMESTAMP)
  -- password_hash column exists but NEVER select it
  -- Sample user_id formats: 'RET_17850', 'CB_119', or UUID strings

stores (store_id VARCHAR PK, owner_id VARCHAR FK→users.user_id,
        store_name VARCHAR, description TEXT, status VARCHAR,
        store_rating NUMERIC, created_at TIMESTAMP)
  -- Sample store_id: 'STORE_D1C51E'

categories (category_id VARCHAR PK, name VARCHAR, slug VARCHAR,
            icon_url VARCHAR, parent_id VARCHAR FK→categories.category_id)
  -- Sample category_id: 'CAT_P1', 'CAT_C3'

products (product_id VARCHAR PK, store_id VARCHAR FK→stores.store_id,
          category_id VARCHAR FK→categories.category_id,
          name VARCHAR, description TEXT, sku VARCHAR,
          base_price NUMERIC, cost_of_product NUMERIC, is_active BOOLEAN)
  -- Sample product_id: 'PROD_XXXXXX'

inventory (inventory_id VARCHAR PK, product_id VARCHAR FK→products.product_id,
           quantity INTEGER, status VARCHAR, low_stock_threshold INTEGER,
           bin_location VARCHAR, last_stock_update TIMESTAMP)

orders (order_id VARCHAR PK, user_id VARCHAR FK→users.user_id,
        store_id VARCHAR FK→stores.store_id,
        order_number VARCHAR, status VARCHAR, fulfilment VARCHAR,
        total_amount NUMERIC, order_date TIMESTAMP, coupon_id VARCHAR)
  -- Sample order_id: 'ORD_00FEE690'
  -- orders has store_id directly — no need to join through products

order_items (order_item_id VARCHAR PK, order_id VARCHAR FK→orders.order_id,
             product_id VARCHAR FK→products.product_id,
             quantity INTEGER, unit_price_at_sale NUMERIC, total_item_price NUMERIC)

payments (payment_id VARCHAR PK, order_id VARCHAR FK→orders.order_id,
          payment_method VARCHAR, payment_status VARCHAR,
          transaction_id VARCHAR, paid_at TIMESTAMP)

shipments (shipment_id VARCHAR PK, order_id VARCHAR FK→orders.order_id,
           carrier_name VARCHAR, tracking_number VARCHAR,
           mode_of_shipment VARCHAR, shipping_status VARCHAR,
           warehouse_block VARCHAR, estimated_delivery TIMESTAMP,
           address_id VARCHAR)

carts (cart_id VARCHAR PK, user_id VARCHAR FK→users.user_id,
       created_at TIMESTAMP, updated_at TIMESTAMP)

cart_items (cart_item_id VARCHAR PK, cart_id VARCHAR FK→carts.cart_id,
            product_id VARCHAR FK→products.product_id, quantity INTEGER)

reviews (review_id VARCHAR PK, product_id VARCHAR FK→products.product_id,
         user_id VARCHAR FK→users.user_id,
         star_rating INTEGER CHECK BETWEEN 1 AND 5, comment TEXT,
         sentiment VARCHAR, is_verified_purchase BOOLEAN,
         helpful_votes INTEGER, created_at TIMESTAMP,
         store_owner_reply TEXT, replied_at TIMESTAMP)

ROLE-BASED ACCESS RULES:
- CUSTOMER: can only query their own data.
    orders/payments/shipments: WHERE user_id = '<their user_id>'
    cart_items: JOIN carts ON cart_items.cart_id = carts.cart_id WHERE carts.user_id = '<their user_id>'
    reviews: WHERE user_id = '<their user_id>'

- CORPORATE: can only query data for their own store.
    products: WHERE store_id = '<their store_id>'
    orders: WHERE store_id = '<their store_id>'
    order_items: JOIN orders ON order_items.order_id = orders.order_id WHERE orders.store_id = '<their store_id>'
    inventory: JOIN products ON inventory.product_id = products.product_id WHERE products.store_id = '<their store_id>'
    reviews: JOIN products ON reviews.product_id = products.product_id WHERE products.store_id = '<their store_id>'

- ADMIN: full access to all tables, no restrictions.

IMPORTANT RULES:
- NEVER select or expose the password_hash column.
- ALL IDs are VARCHAR — never cast with ::uuid.
- Always apply role-based WHERE filters.
- Use ILIKE for case-insensitive text search.
- For date ranges use: order_date, created_at, paid_at columns as appropriate.
- Prefer CTEs for multi-step queries.
- Always add LIMIT 200 if query might return many rows.

DATA INTEGRITY NOTE — CRITICAL:
  order_items.product_id values mostly DO NOT match products.product_id in the products table.
  Therefore:
  - For REVENUE/SALES analysis: always use orders.total_amount directly. Do NOT join order_items→products for revenue.
  - For ORDER COUNT/STATUS: use the orders table directly.
  - For PRODUCT CATALOG: query products table directly (do NOT join with order_items).
  - For WEEKLY/MONTHLY REVENUE: GROUP BY DATE_TRUNC('week', order_date) or DATE_TRUNC('month', order_date) on the orders table.
  - Avoid joins between order_items and products unless specifically asked about product details.
  Example for CORPORATE store revenue by week:
    SELECT DATE_TRUNC('week', order_date) AS week, SUM(total_amount) AS revenue, COUNT(*) AS orders
    FROM orders WHERE store_id = '<store_id>' GROUP BY week ORDER BY week DESC LIMIT 12
"""


GREETING_EXAMPLES = [
    "hello", "hi", "merhaba", "selam", "hey", "nasılsın", "how are you",
    "good morning", "günaydın", "iyi günler",
]
