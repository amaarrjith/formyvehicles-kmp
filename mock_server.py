#!/usr/bin/env python3
import json
from http.server import HTTPServer, BaseHTTPRequestHandler

class MockApiHandler(BaseHTTPRequestHandler):
    def _set_headers(self, status=200):
        self.send_response(status)
        self.send_header('Content-Type', 'application/json')
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type, Authorization, Language, X-Simulate')
        self.end_headers()

    def do_OPTIONS(self):
        self._set_headers(200)

    def do_POST(self):
        content_length = int(self.headers.get('Content-Length', 0))
        body_bytes = self.rfile.read(content_length) if content_length > 0 else b""
        body_str = body_bytes.decode('utf-8')
        
        print("\n" + "="*60)
        print(f"📥 RECEIVED POST REQUEST: {self.path}")
        print("-" * 60)
        print("Headers:")
        for k, v in self.headers.items():
            print(f"  {k}: {v}")
        print("Body:")
        print(body_str)
        print("="*60)

        # Check for simulation flags (e.g. if name is "parse_error" or header X-Simulate: parse_error)
        is_parse_error_test = "parse_error" in body_str.lower() or self.headers.get("X-Simulate") == "parse_error" or "parse_error" in self.path

        if is_parse_error_test:
            print("⚠️ SIMULATING PARSING ERROR: Sending invalid JSON response...")
            self._set_headers(200)
            invalid_json_payload = "{ \"hasError\": false, \"response\": INVALID_UNQUOTED_JSON_STRING }"
            self.wfile.write(invalid_json_payload.encode('utf-8'))
            print("📤 INVALID JSON RESPONSE SENT")
            print("="*60 + "\n")
            return

        self._set_headers(200)

        if "registration" in self.path or "register" in self.path:
            response_data = {
                "hasError": False,
                "errorCode": None,
                "message": "Registration successful",
                "response": {
                    "tempUserId": 101,
                    "email": "registered_user@example.com",
                    "message": "OTP has been sent to your mobile number."
                }
            }
        elif "login" in self.path:
            response_data = {
                "hasError": False,
                "errorCode": None,
                "message": "Login successful",
                "response": {
                    "access": "mock_access_token_abc123",
                    "refresh": "mock_refresh_token_xyz789",
                    "tokenExpiry": 3600,
                    "user": {
                        "id": 101,
                        "name": "Test User",
                        "email": "user@example.com",
                        "mobileNumber": "1234567890"
                    }
                }
            }
        elif "verify" in self.path or "otp" in self.path:
            response_data = {
                "hasError": False,
                "errorCode": None,
                "message": "OTP verified successfully",
                "response": {
                    "access": "mock_access_token_abc123",
                    "refresh": "mock_refresh_token_xyz789",
                    "user": {
                        "id": 101,
                        "name": "Test User"
                    }
                }
            }
        else:
            response_data = {
                "hasError": False,
                "errorCode": None,
                "message": "Success",
                "response": "OK"
            }

        response_bytes = json.dumps(response_data, indent=2).encode('utf-8')
        self.wfile.write(response_bytes)
        print(f"📤 RESPONSE SENT (200 OK):")
        print(json.dumps(response_data, indent=2))
        print("="*60 + "\n")

    def do_GET(self):
        print(f"\n[Mock Server] Received GET {self.path}")
        self._set_headers(200)
        self.wfile.write(json.dumps({"status": "Mock API Server active", "path": self.path}).encode('utf-8'))

def run(port=8000):
    server_address = ('0.0.0.0', port)
    httpd = HTTPServer(server_address, MockApiHandler)
    print(f"==================================================")
    print(f"🚀 MOCK BACKEND SERVER RUNNING AT: http://localhost:{port}/api/")
    print(f"  - POST /api/user/register (or /api/user/registration)")
    print(f"  - POST /api/user/login")
    print(f"  - POST /api/user/email-verify")
    print(f"  - 💡 Tip: Type 'parse_error' in Name field to test parsing error!")
    print(f"==================================================")
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        print("\nStopping server...")
        httpd.server_close()

if __name__ == '__main__':
    run()
