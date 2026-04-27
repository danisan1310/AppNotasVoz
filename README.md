OBJETIVO
Desarrollar una aplicación Android que permita al usuario grabar, reproducir y gestionar notas de voz personales, aplicando navegación, permisos, almacenamiento interno y reproducción multimedia.

CONTEXTO
El usuario quiere una app sencilla para guardar ideas rápidas en formato de audio (como un diario o bloc de notas de voz).

La aplicación permitirá:

Grabar audios
Reproducirlos
Guardarlos en el dispositivo
Ver información básica de cada grabación
REQUISITOS DEL PROYECTO

ESTRUCTURA GENERAL
La app debe tener al menos dos pantallas:

Pantalla principal (lista de audios)
Pantalla de grabación y reproducción
Se debe usar navegación entre pantallas.

MODELO DE DATOS
Debes definir una estructura lógica para representar cada audio.

Cada elemento debe tener al menos:

id (String)
nombre (String)
ruta del archivo (String o File)
fecha de creación
PANTALLA PRINCIPAL
Debe mostrar:

Lista de audios guardados
Nombre de cada audio
Botón para crear una nueva grabación
Funcionalidades:

Navegar a grabar audio
Seleccionar un audio para reproducir
PANTALLA DE AUDIO
Debe permitir:

Preparar audio
Reproducir audio
Pausar
Detener
Debe mostrar:

Estado actual (preparado, reproduciendo, pausado, etc.)
Nombre del archivo
PERMISOS
Debes gestionar correctamente:

Permiso de micrófono
Requisitos:

Solicitar permiso en runtime
Controlar si el usuario lo deniega
GESTIÓN DE FICHEROS
Debes guardar los audios en almacenamiento interno.

Requisitos:

Usar context.filesDir
Generar nombres de archivo automáticamente (por ejemplo con fecha/hora)
Poder acceder posteriormente a esos archivos
REPRODUCCIÓN DE AUDIO
Debes implementar un reproductor de audio.

Requisitos:

Cargar audio desde archivo
Detectar cuándo termina la reproducción
Controlar errores (archivo inexistente, etc.)
MANEJO DE ERRORES
Debes controlar:

Permisos no concedidos
Archivo no encontrado
Errores en reproducción
Mostrar mensajes claros al usuario.

NAVEGACIÓN
Debes implementar navegación entre pantallas:

Pantalla principal → pantalla de audio
Volver atrás
ESTADO EN COMPOSE
Debes usar estado para:

Mostrar estado del audio
Actualizar la interfaz dinámicamente
Controlar cambios en la UI
