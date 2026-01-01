import jwt
import base64
import time
import argparse

# Secreto obtenido de application-dev.yml
SECRET_B64 = "MjY1NjNlNmM2MDkwMGMxODgxOWYxYjk5NzUwNzhiNGY5ZjY1OTk1NzBiNjdlOGU1MjFkYWVlMTlmMmRhZGI1ZTYwZWM3ZjQ4YjhmZWI1YzNiYzM5ZWExZmI0ODEzOTZmZjc0ZDYwN2M0ODVkYTVhNWM1ZGJhYzE3YTMxZGQ2YTM="

def generate_token(username, user_id=1001):
    secret = base64.b64decode(SECRET_B64)
    
    payload = {
        "sub": username,
        "exp": int(time.time()) + 86400, # 24 horas
        "auth": "ROLE_USER,ROLE_ADMIN",
        "iat": int(time.time()),
        "userId": user_id
    }
    
    token = jwt.encode(payload, secret, algorithm="HS512")
    return token

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Genera un token JWT para el Backend")
    parser.add_argument("--user", default="celi", help="Nombre de usuario (default: celi)")
    parser.add_argument("--id", type=int, default=1001, help="ID del usuario (default: 1001)")
    
    args = parser.parse_args()
    
    try:
        token = generate_token(args.user, args.id)
        print("\nToken JWT generado exitosamente para el usuario:", args.user)
        print("-" * 50)
        print(token)
        print("-" * 50)
        print("\nPuedes usarlo en Postman como: Authorization: Bearer <TOKEN>")
    except Exception as e:
        print("Error al generar el token:", e)
