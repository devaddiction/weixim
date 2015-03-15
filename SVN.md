# Introducción #

Esta página pretende servir de tutorial para poder trabajar con SVN. Se recomienda el uso de TortoiseSVN o Subclipse (como plugin para Eclipse).


# Instalación #

Se va a recomendar, por ser el más intuitivo, el cliente Tortoise SVN que podeis bajaros de la web de TortoiseSVN http://tortoisesvn.net/downloads

Instalais el programa y continuamos con el manual.

He añadido como "Project committers", ya que si no podríais ver el repositorio, pero no subir cambios a él. Una vez que esteis dados de alta en el grupo de google code, teneis que meteros "logueados" en la web del proyecto y seguir los siguientes pasos:

- Seleccionar la pestaña "Source".

- Luego, para obtener la contraseña le daremos al siguiente enlace “When prompted, enter your generated googlecode.com password“.

- Copiamos la contraseña que nos ha dado.

- Ahora hay que crear el repositorio. Para ello 1º creamos una carpeta donde queramos. Sobre ella le damos boton derecho y a la opcion "SVN Checkout".

- Nos pedira algunos datos. En la url del repositorio introducir https://weixim.googlecode.com/svn/trunk/

- Cuando pida usuario y contraseña, meter como usuaro vuesta direccion de gmail y como contraseña la que habeis obtenido del paso anterior. Darle a "Save Authentication" para que no tengais que volver a meter la contraseña.

- Y ya está, ya teneis vuestro repo SVN en vuestro ordenador con Google Code.

Para obtener el código desde el repositorio, usaremos el comando **CHECKOUT** (si es la primera vez que lo bajamos), o **UPDATE** (si estamos actualizando desde el código del servidor). Si queremos subir una copia (estable) de un fichero local, debemos hacerlo mediante el comando **COMMIT** (añadiendo una descripción, si es posible).