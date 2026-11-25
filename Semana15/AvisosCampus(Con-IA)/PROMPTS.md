# Prompts y decisiones de la fase 2

La IA propuso alternativas. La implementación final conserva MVVM + Repository, mantiene el código verificable y se probó en un emulador Android 16.

| Prompt que escribí | Qué respondió la IA | Qué cambié o descarté | Por qué |
| --- | --- | --- | --- |
| «Añade dos canales para AvisosCampus y usa Urgentes solo para los recordatorios.» | Propuso crear dos `NotificationChannel` desde `Application` y elegir el canal según el origen del aviso. | Conservé dos canales y mantuve el recordatorio con `IMPORTANCE_HIGH`; los avisos manuales conservan `IMPORTANCE_DEFAULT`. | La importancia pertenece al canal del sistema y el recordatorio es el único evento urgente del laboratorio. |
| «Escucha `ACTION_POWER_CONNECTED` y `ACTION_POWER_DISCONNECTED` con un interruptor independiente.» | Propuso un `BroadcastReceiver` registrado desde la pantalla y un estado separado en el ViewModel. | Añadí `CargadorReceiver`, un `StateFlow` propio y `DisposableEffect` con `RECEIVER_NOT_EXPORTED`. | El receptor queda activo solo mientras el usuario lo solicita y se libera al salir de la composición. |
| «Persiste el historial con DataStore sin poner Context o NotificationManager en el ViewModel.» | Propuso un repositorio persistente y una instancia de DataStore asociada a la aplicación. | Implementé `AvisosRepositoryPersistente` con Preferences DataStore y codificación UTF-8 por campo. | El repositorio controla almacenamiento y el ViewModel continúa testeable mediante la interfaz. |
| «Revisa las propuestas y busca fugas de receptores o dependencias de Android en el ViewModel.» | Indicó comprobar el ciclo de vida del receptor y mantener las llamadas del sistema fuera del ViewModel. | Corregí cualquier registro para que tenga `onDispose` y mantuve el acceso a contexto en `PantallaAvisos` y el repositorio. | Cumple la regla arquitectónica de la guía y evita que la pantalla retenga una Activity. |

## Criterio de revisión

- El ViewModel solo decide qué aviso registrar y expone estados y eventos.
- `PantallaAvisos` registra ambos receptores con `ContextCompat.registerReceiver` y los retira en `onDispose`.
- El canal `Urgentes` se reserva para el origen `RECORDATORIO`.
- DataStore guarda el historial después de cada registro y lo restaura al iniciar.
