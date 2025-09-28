package com.example.services.data

data class FeaturedAct(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val date: String,      // "YYYY-MM-DD"
    val categoria: String,
    val ubicacion: String,
    val imageUrl: String
)

object FeaturedActs {
    val items = listOf(
        FeaturedAct(
            id = "act-playas",
            titulo = "Limpieza de playas",
            descripcion = "Unete para limpiar playas locales y proteger la vida marina.",
            date = "2025-10-12",
            categoria = "Medio ambiente",
            ubicacion = "Playa Miramar",
            imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?q=80&w=1200"
        ),
        FeaturedAct(
            id = "act-reforestacion",
            titulo = "Reforestacion",
            descripcion = "Ayuda plantando árboles y restaurando ecosistemas.",
            date = "2025-11-02",
            categoria = "Medio ambiente",
            ubicacion = "Sierra de Álvarez",
            imageUrl = "https://images.unsplash.com/photo-1469474968028-56623f02e42e?q=80&w=1200"
        ),
        FeaturedAct(
            id = "act-refugios",
            titulo = "Voluntariado en refugios",
            descripcion = "Apoya a refugios cuidando y socializando con animales.",
            date = "2025-10-20",
            categoria = "Animales",
            ubicacion = "Refugio San Angel",
            imageUrl = "https://images.unsplash.com/photo-1574158622682-e40e69881006?q=80&w=1200"
        ),
        FeaturedAct(
            id = "act-apoyo-escolar",
            titulo = "Clases de apoyo escolar",
            descripcion = "Brinda tutorías y acompañamiento académico a niños.",
            date = "2025-10-28",
            categoria = "Educacion",
            ubicacion = "Centro Comunitario",
            imageUrl = "https://images.unsplash.com/photo-1513258496099-48168024aec0?q=80&w=1200"
        ),
        FeaturedAct(
            id = "act-donacion",
            titulo = "Campañas de donacion",
            descripcion = "Recolecta alimentos y ropa para familias vulnerables.",
            date = "2025-12-05",
            categoria = "Comunidad",
            ubicacion = "Plaza Central",
            imageUrl = "https://plus.unsplash.com/premium_photo-1683134261048-dfb427f8becf?w=400&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8ZG9uYWNpb25lc3xlbnwwfHwwfHx8MA%3D%3D"
        )
    )
}