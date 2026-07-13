# Guía: ¿Cómo agregar las imágenes de los productos manualmente?

Para que el sistema asocie automáticamente las imágenes reales de tus productos a las tarjetas del módulo de **Ventas**, sigue estos sencillos pasos:

## 📂 Directorio de Destino
Todas las imágenes deben guardarse en la siguiente carpeta del frontend:
`frontend/public/assets/productos/`

> Si la carpeta `productos` no existe dentro de `frontend/public/assets/`, puedes crearla manualmente.

---

## 🏷️ Regla de Nombre del Archivo
El sistema utiliza una función de normalización que elimina espacios, acentos y convierte el texto a minúsculas. Debes nombrar tu archivo de imagen según el nombre del producto, reemplazando los espacios por guiones bajos (`_`) y en formato **WebP** o **PNG** (el sistema está configurado por defecto para buscar `.webp`, pero si usas otra extensión puedes subirla y actualizar la URL en la base de datos).

### Ejemplos de mapeo:

| Nombre del producto en el Inventario | Nombre exacto del archivo de imagen | Ruta completa en el proyecto |
| :--- | :--- | :--- |
| **Aceite Vegetal** | `aceite_vegetal.webp` | `frontend/public/assets/productos/aceite_vegetal.webp` |
| **Piqueo Snax** o **PIQuéO Snax** | `piqueo_snax.webp` | `frontend/public/assets/productos/piqueo_snax.webp` |
| **Cerveza Pilsen** | `cerveza_pilsen.webp` | `frontend/public/assets/productos/cerveza_pilsen.webp` |
| **Pan Molde** | `pan_molde.webp` | `frontend/public/assets/productos/pan_molde.webp` |

---

## ⚙️ ¿Cómo funciona la asociación automática?
Cuando creas un producto en el módulo **Inventario**, por ejemplo: `Aceite Vegetal 1L`:
1. El backend normaliza el nombre a `aceite_vegetal_1l`.
2. Asigna automáticamente la URL: `/assets/productos/aceite_vegetal_1l.webp`.
3. Si colocas un archivo con ese nombre exacto en la carpeta `frontend/public/assets/productos/`, la imagen se mostrará automáticamente en el módulo de **Ventas**.
4. Si no agregas ninguna imagen a la carpeta, el sistema mostrará automáticamente una imagen de marcador de posición (marcando "Sin Imagen" con un estilo premium).

---

## 🛠️ ¿Cómo convertir tus imágenes a formato WebP?
WebP es el formato estándar recomendado para aplicaciones web modernas por su excelente compresión sin pérdida de calidad.
* Puedes usar convertidores gratuitos online como [ezgif.com/jpg-to-webp](https://ezgif.com/jpg-to-webp) o [squoosh.app](https://squoosh.app).
* Guarda tus imágenes con las dimensiones sugeridas de **300x300 píxeles** o similares (relación de aspecto 1:1) para que se vean simétricas y perfectas en el panel.
