# Introducción #
El sistema de tickets es una forma sencilla y simple para mantener un registro del estado de fallas, errores o peticiones de mejora a un programa. Dicha herramienta posibilita una comunicación fluida entre los desarrolladores a la vez de sentar las bases para las soluciones.

# Uso #
Se puede acceder al sistema de tickets mediante la pestaña issues. Por defecto muestra la pantalla de tickets existentes, la cual se debe revisar antes de crear uno nuevo. Para dicha tarea se puede hacer uso de la casilla de búsqueda.

Para crear un nuevo ticket vea el siguiente apartado.

# Nuevo ticket #
Si se quiere crear un nuevo ticket, debe irse al link de new issue.

# Estructura básica del editor #
El editor contiene campos para los nuevos tipos de tickets, entre los que se cuentan templates, título, cuerpo y etiquetas.

# Templates #
Los templates, o en Español, plantillas, son modelos de ticket para agilizar la tarea de redacción. Contienen varios tipos de información que son esenciales para diagnosticar los fallos.

Nombre del template - Usado en caso de...  - Autorizado para...

---

Defect report from user - Reporte de error general  - Todos

Defect report from developer - Reporte de error general - Sólo desarrolladores y miembros

Review request - Para sugerir modificaciones de código o aportes - Sólo desarrolladores y miembros del grupo

Ehancement - Mejora de un programa o plugin - Todos

Dato incorrecto en un plugin - Reportar un dato erróneo en un plugin (sólo información, no errores de funcionamiento) - Todos

Error en plugin - Reporte de un comportamiento erróneo o fuera del pretendido - Todos

Dato incorrecto en un plugin - Reportar un dato erróneo en un plugin (sólo información, no errores de funcionamiento) - Todos

Error en plugin -  Reporte de un comportamiento erróneo o fuera del pretendido - Todos


# Descripción #
En la descripción se encuentra el cuerpo del texto y el template elegido. Allí debe colocarse la información según el template determine, y toda la información extra debe quedar asentada al final. Se sugiere el uso de descripciones cortas, claras y concisas, pero sin perder detalle.

# Título #
Debe ser descriptivo y corto: define el error en pocas palabras. No debe contener las palabras "error", "mejora" o "problema", ya que las etiquetas se encargan de clasificar los datos

# Incluir un archivo #
El sistema de tickets da la posibilidad de incluir un archivo junto con el reporte. Es una buena sugerencia colocar los logs de errores o correcciones en los datos allí (por ejemplo, una base de datos actualizada).