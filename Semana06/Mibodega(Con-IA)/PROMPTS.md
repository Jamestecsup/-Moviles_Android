# Historial de Prompts - Fase 2 MiBodega (Mejora con IA)

> Nota: la base ya traía badge en el carrito y buscador en tiempo real
> combinado con categorías; eso no se tocó.

## Prompt 1: Login con usuario y clave fijos
- **Fecha**: 2026-10-03
- **Prompt**:
  > "Agrega campo de contraseña a `LoginScreen.kt`, valida contra un usuario y clave fijos en el código (`987654321` / `tecsup123`) y muestra el error en rojo si no coinciden. Agrega `esContrasena` a `CampoTexto.kt` con `PasswordVisualTransformation`."

## Prompt 2: Campos en rojo si están vacíos
- **Fecha**: 2026-10-03
- **Prompt**:
  > "Agrega `esError` a `CampoTexto.kt` con `isError` y mensaje 'Completa este campo'. En Registro, Entrega y Login marca en rojo los vacíos al intentar enviar (`intentoEnviar`) y no avances hasta que estén llenos."

## Prompt 3: Carrito vacío y confirmar antes de eliminar
- **Fecha**: 2026-10-03
- **Prompt**:
  > "En `CarritoScreen.kt` muestra 'Tu carrito está vacío' cuando no hay productos (ocultando el resumen) y pide confirmación con un `AlertDialog` antes de eliminar un producto."
