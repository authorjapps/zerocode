"""Generate dummy Zerocode raw-format scenario reports for the SFEO preview.

Usage (uv venv): .venv/bin/python -I scripts/gen_dummy.py examples/dummy_scenarios
Deterministic: fixed UUIDs (uuid5) and fixed timestamps.
"""
import json
import os
import sys
import uuid
from datetime import datetime, timedelta

BASE_URL = "https://api.example-shop.test"
NAMESPACE = uuid.UUID("6f1c2a52-0d7e-4c55-9b0e-5a1d8f3e2b10")


def pretty(obj):
    # Jackson-like pretty print: "key" : value
    return json.dumps(obj, indent=2, separators=(",", " : "))


def stamp(moment):
    return moment.strftime("%Y-%m-%dT%H:%M:%S.") + f"{moment.microsecond // 1000:03d}"


def corr_id(label):
    return str(uuid.uuid5(NAMESPACE, label))


def rest_response(status, body):
    headers = {"Content-Type": ["application/json; charset=utf-8"], "Server": ["example-shop"]}
    return {"status": status, "headers": headers, "body": body}


def failed_assertions(expected, errors):
    lines = "".join(
        f"Assertion jsonPath '{path}' with actual value '{actual}' did not match the expected value '{wanted}'\n"
        for path, actual, wanted in errors
    )
    return "Assumed Payload: \n" + pretty(expected) + "\nAssertion Errors: \n" + lines


class ScenarioBuilder:
    def __init__(self, name, start):
        self.name = name
        self.cursor = start
        self.steps = []

    def attempt(self, step, delay_ms, result, request, response, assertions, wait_after_ms=15):
        request_time = self.cursor
        response_time = request_time + timedelta(milliseconds=delay_ms)
        self.steps.append({
            "name": step["name"],
            "url": step["url"],
            "correlationId": step["correlationId"],
            "operation": step["operation"],
            "requestTimeStamp": stamp(request_time),
            "responseTimeStamp": stamp(response_time),
            "responseDelay": float(delay_ms),
            "result": result,
            "request": pretty(request),
            "response": pretty(response),
            "assertions": assertions,
        })
        self.cursor = response_time + timedelta(milliseconds=wait_after_ms)

    def passed(self, name, url, operation, delay_ms, request, response, expected):
        step = {"name": name, "url": url, "operation": operation,
                "correlationId": corr_id(self.name + "/" + name)}
        self.attempt(step, delay_ms, "PASSED", request, response, pretty(expected))

    def retried(self, name, url, operation, request, expected, attempts, retry_delay_ms):
        """attempts: list of (delay_ms, response, errors). errors empty => PASSED."""
        step = {"name": name, "url": url, "operation": operation,
                "correlationId": corr_id(self.name + "/" + name)}
        for delay_ms, response, errors in attempts:
            if errors:
                assertions = failed_assertions(expected, errors)
                self.attempt(step, delay_ms, "FAILED", request, response, assertions, retry_delay_ms)
                continue
            self.attempt(step, delay_ms, "PASSED", request, response, pretty(expected))

    def report(self, time_stamp):
        return {"timeStamp": stamp(time_stamp),
                "results": [{"scenarioName": self.name, "steps": self.steps, "loop": 0}]}


JSON_HEADERS = {"Content-Type": "application/json"}
AUTH_HEADERS = {"Content-Type": "application/json", "Authorization": "Bearer ${$.login.response.body.token}"}


def scenario_customer(start):
    builder = ScenarioBuilder("Create and fetch customer", start)
    customer = {"id": "C-1001", "name": "Ada Lovelace", "email": "ada@example-shop.test", "tier": "GOLD"}
    builder.passed("create_customer", BASE_URL + "/customers", "POST", 212,
                   {"headers": JSON_HEADERS, "body": {"name": "Ada Lovelace", "email": "ada@example-shop.test"}},
                   rest_response(201, customer),
                   {"status": 201, "body": {"id": "$NOT.NULL", "name": "Ada Lovelace"}})
    builder.passed("get_customer", BASE_URL + "/customers/C-1001", "GET", 88,
                   {"headers": JSON_HEADERS},
                   rest_response(200, customer),
                   {"status": 200, "body": {"id": "C-1001", "tier": "GOLD"}})
    updated = dict(customer, email="ada.l@example-shop.test")
    builder.passed("update_customer_email", BASE_URL + "/customers/C-1001", "PUT", 141,
                   {"headers": JSON_HEADERS, "body": {"email": "ada.l@example-shop.test"}},
                   rest_response(200, updated),
                   {"status": 200, "body": {"email": "ada.l@example-shop.test"}})
    return builder


def scenario_order(start):
    builder = ScenarioBuilder("Place order end to end", start)
    builder.passed("login", BASE_URL + "/auth/token", "POST", 174,
                   {"headers": JSON_HEADERS, "body": {"username": "ada", "password": "******"}},
                   rest_response(200, {"token": "eyJhbGciOi.dummy.token", "expiresIn": 3600}),
                   {"status": 200, "body": {"token": "$NOT.NULL"}})
    product = {"id": "P-77", "title": "Mechanical keyboard", "price": 89.99, "stock": 12}
    builder.passed("get_product", BASE_URL + "/products/P-77", "GET", 63,
                   {"headers": AUTH_HEADERS}, rest_response(200, product),
                   {"status": 200, "body": {"id": "P-77", "stock": "$GT.0"}})
    builder.passed("add_to_cart", BASE_URL + "/carts/CART-1/items", "POST", 119,
                   {"headers": AUTH_HEADERS, "body": {"productId": "P-77", "qty": 2}},
                   rest_response(201, {"cartId": "CART-1", "items": [{"productId": "P-77", "qty": 2}]}),
                   {"status": 201, "body": {"items.SIZE": 1}})
    cart = {"cartId": "CART-1", "items": [{"productId": "P-77", "qty": 2, "lineTotal": 179.98}], "total": 179.98}
    builder.passed("get_cart", BASE_URL + "/carts/CART-1", "GET", 57,
                   {"headers": AUTH_HEADERS}, rest_response(200, cart),
                   {"status": 200, "body": {"total": 179.98}})
    builder.passed("apply_discount", BASE_URL + "/carts/CART-1/discount", "POST", 96,
                   {"headers": AUTH_HEADERS, "body": {"code": "AUTUMN10"}},
                   rest_response(200, {"cartId": "CART-1", "discount": 18.0, "total": 161.98}),
                   {"status": 200, "body": {"total": 161.98}})
    builder.passed("checkout", BASE_URL + "/orders", "POST", 305,
                   {"headers": AUTH_HEADERS, "body": {"cartId": "CART-1", "shipTo": "1 Analytical St, London"}},
                   rest_response(202, {"orderId": "ORD-5001", "status": "CREATED"}),
                   {"status": 202, "body": {"orderId": "$NOT.NULL", "status": "CREATED"}})
    builder.passed("pay_order", BASE_URL + "/payments", "POST", 412,
                   {"headers": AUTH_HEADERS, "body": {"orderId": "ORD-5001", "amount": 161.98, "card": "**** 4242"}},
                   rest_response(200, {"paymentId": "PAY-9001", "status": "CAPTURED"}),
                   {"status": 200, "body": {"status": "CAPTURED"}})
    shipped_expected = {"status": 200, "body": {"orderId": "ORD-5001", "status": "SHIPPED"}}
    pending = rest_response(200, {"orderId": "ORD-5001", "status": "PENDING", "carrier": None})
    shipped = rest_response(200, {"orderId": "ORD-5001", "status": "SHIPPED", "carrier": "DHL", "tracking": "DHL-7781"})
    status_error = [("$.body.status", "PENDING", "SHIPPED")]
    builder.retried("order_status_shipped", BASE_URL + "/orders/ORD-5001", "GET",
                    {"headers": AUTH_HEADERS}, shipped_expected,
                    [(71, pending, status_error), (68, pending, status_error), (74, shipped, [])],
                    retry_delay_ms=2000)
    builder.passed("get_invoice", BASE_URL + "/orders/ORD-5001/invoice", "GET", 133,
                   {"headers": AUTH_HEADERS},
                   rest_response(200, {"invoiceId": "INV-3301", "orderId": "ORD-5001", "total": 161.98}),
                   {"status": 200, "body": {"invoiceId": "$NOT.NULL"}})
    builder.passed("send_confirmation_email", BASE_URL + "/notifications", "POST", 158,
                   {"headers": AUTH_HEADERS, "body": {"to": "ada.l@example-shop.test", "template": "ORDER_SHIPPED"}},
                   rest_response(202, {"notificationId": "N-12", "queued": True}),
                   {"status": 202, "body": {"queued": True}})
    return builder


def kafka_produce(key, value):
    request = {"recordType": "JSON", "records": [{"key": key, "value": value}]}
    response = {"status": "Ok", "recordMetadata": {"topicPartition": {"partition": 0, "topic": "orders.v1"},
                                                  "offset": 4182}}
    return request, response


def kafka_consume(key, value):
    request = {"consumerLocalConfigs": {"recordType": "JSON", "commitSync": True, "maxNoOfRetryPollsOrTimeouts": 3}}
    response = {"records": [{"key": key, "value": value}], "size": 1}
    return request, response


def scenario_kafka(start):
    builder = ScenarioBuilder("Publish order event to Kafka", start)
    topic = "kafka-topic:orders.v1"
    created = {"type": "ORDER_CREATED", "orderId": "ORD-5001", "total": 161.98}
    shipped = {"type": "ORDER_SHIPPED", "orderId": "ORD-5001", "carrier": "DHL"}
    request, response = kafka_produce("ORD-5001", created)
    builder.passed("produce_order_created", topic, "produce", 245, request, response, {"status": "Ok"})
    request, response = kafka_consume("ORD-5001", created)
    builder.passed("consume_order_created", topic, "consume", 1312, request, response,
                   {"size": 1, "records": [{"value": {"type": "ORDER_CREATED"}}]})
    request, response = kafka_produce("ORD-5001", shipped)
    builder.passed("produce_order_shipped", topic, "produce", 198, request, response, {"status": "Ok"})
    request, response = kafka_consume("ORD-5001", shipped)
    builder.passed("consume_order_shipped", topic, "consume", 1104, request, response,
                   {"size": 1, "records": [{"value": {"type": "ORDER_SHIPPED"}}]})
    return builder


def scenario_db(start):
    builder = ScenarioBuilder("Verify order row in DB", start)
    url = "org.jsmart.zerocode.core.db.DbSqlExecutor"
    builder.passed("select_order_row", url, "EXECUTE", 46,
                   {"sql": "SELECT id, status, total FROM orders WHERE id = ?", "parameters": ["ORD-5001"]},
                   {"rows": [{"ID": "ORD-5001", "STATUS": "SHIPPED", "TOTAL": 161.98}]},
                   {"rows.SIZE": 1, "rows": [{"STATUS": "SHIPPED"}]})
    builder.passed("select_order_lines", url, "EXECUTE", 39,
                   {"sql": "SELECT product_id, qty FROM order_lines WHERE order_id = ?", "parameters": ["ORD-5001"]},
                   {"rows": [{"PRODUCT_ID": "P-77", "QTY": 2}]},
                   {"rows.SIZE": 1, "rows": [{"QTY": 2}]})
    return builder


def scenario_refund(start):
    builder = ScenarioBuilder("Refund payment", start)
    builder.passed("login", BASE_URL + "/auth/token", "POST", 166,
                   {"headers": JSON_HEADERS, "body": {"username": "ada", "password": "******"}},
                   rest_response(200, {"token": "eyJhbGciOi.dummy.token", "expiresIn": 3600}),
                   {"status": 200, "body": {"token": "$NOT.NULL"}})
    builder.passed("create_refund", BASE_URL + "/refunds", "POST", 287,
                   {"headers": AUTH_HEADERS, "body": {"paymentId": "PAY-9001", "amount": 161.98, "reason": "DAMAGED"}},
                   rest_response(202, {"refundId": "RF-301", "status": "REQUESTED"}),
                   {"status": 202, "body": {"refundId": "$NOT.NULL"}})
    builder.passed("get_refund", BASE_URL + "/refunds/RF-301", "GET", 74,
                   {"headers": AUTH_HEADERS},
                   rest_response(200, {"refundId": "RF-301", "status": "PROCESSING"}),
                   {"status": 200, "body": {"refundId": "RF-301"}})
    completed_expected = {"status": 200, "body": {"refundId": "RF-301", "status": "COMPLETED", "settledAt": "$NOT.NULL"}}
    processing = rest_response(200, {"refundId": "RF-301", "status": "PROCESSING", "settledAt": None})
    errors = [("$.body.status", "PROCESSING", "COMPLETED"), ("$.body.settledAt", "null", "NOT NULL")]
    builder.retried("refund_status_completed", BASE_URL + "/refunds/RF-301", "GET",
                    {"headers": AUTH_HEADERS}, completed_expected,
                    [(81, processing, errors), (77, processing, errors), (90, processing, errors), (85, processing, errors)],
                    retry_delay_ms=3000)
    # Zerocode stops the scenario here; steps 5-6 (get_wallet_balance, notify_customer) never run.
    return builder


def main():
    out_dir = sys.argv[1]
    run_start = datetime(2026, 10, 26, 9, 0, 0, 100000)
    builders = [
        scenario_customer(run_start),
        scenario_order(run_start + timedelta(milliseconds=600)),
        scenario_kafka(run_start + timedelta(seconds=7)),
        scenario_db(run_start + timedelta(seconds=7, milliseconds=900)),  # overlaps the Kafka scenario
        scenario_refund(run_start + timedelta(seconds=10, milliseconds=500)),
    ]
    merged = {"timeStamp": stamp(run_start), "results": []}
    for index, builder in enumerate(builders, start=1):
        report = builder.report(run_start)
        last_corr = builder.steps[-1]["correlationId"]
        file_name = f"{out_dir}/{builder.name}-{last_corr}.json"
        with open(file_name, "w") as handle:
            json.dump(report, handle, indent=4)
        merged["results"].extend(report["results"])
        print(f"{index}. {file_name}  entries={len(builder.steps)}")
    # Kept in a sub-folder so build_report.py on out_dir does not read every scenario twice.
    merged_dir = f"{out_dir}/merged"
    os.makedirs(merged_dir, exist_ok=True)
    with open(f"{merged_dir}/dummy-run-all.json", "w") as handle:
        json.dump(merged, handle, indent=4)
    print(f"merged -> {merged_dir}/dummy-run-all.json")


if __name__ == "__main__":
    main()
