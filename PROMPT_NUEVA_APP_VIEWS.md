# 📱 PROMPT PARA CREAR "SIMPLE AGENDA" CON VIEWS CLÁSICAS

---

## 🎯 CONTEXTO
Tengo una app Android llamada **"Simple Agenda"** desarrollada completamente en Jetpack Compose. Necesito crear una **nueva versión desde cero** usando **Views clásicas (XML)** manteniendo la misma funcionalidad y arquitectura MVVM.

---

## 📋 ESPECIFICACIONES DEL PROYECTO

### **Información básica**
- **Nombre de la app:** Simple Agenda
- **Package:** com.example.simpleagenda
- **Lenguaje:** Kotlin
- **Min SDK:** API 29 (Android 10)
- **Target SDK:** API 35
- **Compile SDK:** API 35
- **Version Code:** 1
- **Version Name:** "1.0"

---

## 🏗️ ARQUITECTURA REQUERIDA

### **MVVM Completo**
```
app/
├── data/
│   ├── local/          # Room Database
│   │   ├── dao/
│   │   │   ├── TaskDao.kt
│   │   │   └── NoteDao.kt
│   │   ├── entities/
│   │   │   ├── TaskEntity.kt
│   │   │   └── NoteEntity.kt
│   │   └── AppDatabase.kt
│   ├── remote/         # Firebase (opcional para auth)
│   └── repository/
│       ├── TaskRepositoryImpl.kt
│       ├── NoteRepositoryImpl.kt
│       └── SettingsRepositoryImpl.kt
├── domain/
│   ├── model/
│   │   ├── Task.kt
│   │   └── Note.kt
│   ├── repository/
│   │   ├── TaskRepository.kt
│   │   ├── NoteRepository.kt
│   │   └── SettingsRepository.kt
│   └── usecase/        # Opcional
├── presentation/
│   ├── tasks/
│   │   ├── TasksFragment.kt
│   │   ├── TasksViewModel.kt
│   │   ├── TasksAdapter.kt
│   │   ├── TaskViewHolder.kt
│   │   └── AddEditTaskFragment.kt
│   ├── notes/
│   │   ├── NotesFragment.kt
│   │   ├── NotesViewModel.kt
│   │   ├── NotesAdapter.kt
│   │   ├── NoteViewHolder.kt
│   │   └── AddEditNoteFragment.kt
│   ├── calendar/
│   │   ├── CalendarFragment.kt
│   │   └── CalendarViewModel.kt
│   ├── settings/
│   │   └── SettingsFragment.kt
│   └── MainActivity.kt
└── ui/
    └── theme/
```

---

## 📦 DEPENDENCIAS NECESARIAS

### **build.gradle.kts (Module: app)**
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.navigation.safeargs)
}

android {
    namespace = "com.example.simpleagenda"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.simpleagenda"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Lifecycle (ViewModel + LiveData)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    // Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.4")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.4")

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // RecyclerView + CardView
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // DataStore (para tema claro/oscuro)
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Fragment KTX
    implementation("androidx.fragment:fragment-ktx:1.8.5")
    implementation("androidx.activity:activity-ktx:1.9.3")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
```

### **libs.versions.toml** (actualizar)
```toml
[versions]
agp = "8.7.3"
kotlin = "2.0.21"
room = "2.6.1"
navigation = "2.8.4"
lifecycle = "2.8.7"

[libraries]
# ... (mantén las existentes y agrega estas)
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
androidx-navigation-fragment-ktx = { group = "androidx.navigation", name = "navigation-fragment-ktx", version.ref = "navigation" }
androidx-navigation-ui-ktx = { group = "androidx.navigation", name = "navigation-ui-ktx", version.ref = "navigation" }

[plugins]
# ... (mantén los existentes y agrega estos)
kotlin-kapt = { id = "org.jetbrains.kotlin.kapt", version.ref = "kotlin" }
navigation-safeargs = { id = "androidx.navigation.safeargs.kotlin", version.ref = "navigation" }
```

---

## 🗄️ MODELOS DE DATOS

### **Task (modelo de dominio)**
```kotlin
data class Task(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val dueAt: Long? = null,        // Timestamp en millis (opcional)
    val priority: Int = 1,          // 1: Baja, 2: Media, 3: Alta
    val done: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
```

### **Note (modelo de dominio)**
```kotlin
data class Note(
    val id: Long = 0,
    val title: String,
    val body: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
```

### **TaskEntity (Room)**
```kotlin
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "due_at") val dueAt: Long?,
    @ColumnInfo(name = "priority") val priority: Int,
    @ColumnInfo(name = "done") val done: Boolean,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
)
```

### **NoteEntity (Room)**
```kotlin
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "body") val body: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
)
```

---

## 🎨 UI CLÁSICA REQUERIDA

### **MainActivity.kt**
- **BottomNavigationView** con 4 tabs:
  1. 📋 Tareas (TasksFragment)
  2. 📅 Calendario (CalendarFragment)
  3. 📝 Notas (NotesFragment)
  4. ⚙️ Configuración (SettingsFragment)
- **NavHostFragment** para navegación
- **Toolbar/AppBar** con título dinámico según fragmento activo

### **activity_main.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.google.android.material.appbar.MaterialToolbar
        android:id="@+id/toolbar"
        android:layout_width="match_parent"
        android:layout_height="?attr/actionBarSize"
        app:layout_constraintTop_toTopOf="parent"
        app:title="Simple Agenda" />

    <androidx.fragment.app.FragmentContainerView
        android:id="@+id/nav_host_fragment"
        android:name="androidx.navigation.fragment.NavHostFragment"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:defaultNavHost="true"
        app:layout_constraintBottom_toTopOf="@id/bottom_navigation"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/toolbar"
        app:navGraph="@navigation/nav_graph" />

    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottom_navigation"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:layout_constraintBottom_toBottomOf="parent"
        app:menu="@menu/bottom_nav_menu" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### **TasksFragment**
- **RecyclerView** con **LinearLayoutManager**
- **TaskAdapter** + **TaskViewHolder**
- **FloatingActionButton** para agregar tarea
- **CardView** para cada item de tarea
- Click en item → navega a **AddEditTaskFragment** (modo edición)
- Click en FAB → navega a **AddEditTaskFragment** (modo creación)
- Swipe o botón en item para eliminar (con confirmación)
- Checkbox para marcar como completada

### **fragment_tasks.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerViewTasks"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:padding="16dp"
        app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager" />

    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/fabAddTask"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|end"
        android:layout_margin="16dp"
        android:contentDescription="Agregar tarea"
        app:srcCompat="@android:drawable/ic_input_add" />

</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

### **item_task.xml** (CardView para RecyclerView)
```xml
<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="8dp"
    app:cardElevation="2dp"
    app:cardCornerRadius="12dp">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:padding="16dp">

        <CheckBox
            android:id="@+id/checkBoxDone"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent" />

        <TextView
            android:id="@+id/tvTitle"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_marginStart="12dp"
            android:textSize="16sp"
            android:textStyle="bold"
            app:layout_constraintEnd_toStartOf="@id/btnDelete"
            app:layout_constraintStart_toEndOf="@id/checkBoxDone"
            app:layout_constraintTop_toTopOf="@id/checkBoxDone" />

        <TextView
            android:id="@+id/tvDescription"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_marginTop="4dp"
            android:textSize="14sp"
            app:layout_constraintEnd_toEndOf="@id/tvTitle"
            app:layout_constraintStart_toStartOf="@id/tvTitle"
            app:layout_constraintTop_toBottomOf="@id/tvTitle" />

        <com.google.android.material.chip.Chip
            android:id="@+id/chipPriority"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="8dp"
            app:layout_constraintStart_toStartOf="@id/tvTitle"
            app:layout_constraintTop_toBottomOf="@id/tvDescription" />

        <ImageButton
            android:id="@+id/btnDelete"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:background="?attr/selectableItemBackgroundBorderless"
            android:contentDescription="Eliminar"
            android:src="@android:drawable/ic_menu_delete"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent" />

    </androidx.constraintlayout.widget.ConstraintLayout>

</com.google.android.material.card.MaterialCardView>
```

### **AddEditTaskFragment**
- Formulario con **TextInputLayout** para:
  - Título (obligatorio)
  - Descripción (opcional)
  - Fecha de vencimiento (DatePicker)
  - Prioridad (Spinner o RadioGroup: Baja/Media/Alta)
- Botones: **Guardar** y **Cancelar**
- Si es edición: cargar datos de la tarea seleccionada

### **NotesFragment**
- Similar a TasksFragment pero con notas
- **RecyclerView** + **NoteAdapter** + **NoteViewHolder**
- **FloatingActionButton** para agregar nota
- Click en item → navega a **AddEditNoteFragment**

### **CalendarFragment**
- Vista simple de calendario (puedes usar MaterialCalendarView o custom view)
- Mostrar tareas del día seleccionado en un **RecyclerView** debajo

### **SettingsFragment**
- **Switch/Toggle** para tema claro/oscuro (guardar en DataStore)
- Texto informativo: "Simple Agenda v1.0"
- (Opcional) Botón "Acerca de"

---

## 🎨 TEMA Y COLORES

### **Paleta verde profesional (heredada de la app actual)**

#### **colors.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- Primarios -->
    <color name="green_500">#43A047</color>
    <color name="green_400">#66BB6A</color>
    <color name="green_700">#388E3C</color>

    <!-- Secundarios -->
    <color name="teal_500">#26A69A</color>
    <color name="teal_400">#4DB6AC</color>
    <color name="teal_700">#00897B</color>

    <!-- Superficies -->
    <color name="surface_light">#FAFAFA</color>
    <color name="surface_dark">#121212</color>
    <color name="on_surface_light">#1B1B1B</color>
    <color name="on_surface_dark">#E0E0E0</color>

    <!-- Estado -->
    <color name="priority_high">#EF5350</color>
    <color name="priority_medium">#FFA726</color>
    <color name="priority_low">#66BB6A</color>

    <color name="white">#FFFFFF</color>
    <color name="black">#000000</color>
</resources>
```

#### **themes.xml** (res/values/)
```xml
<resources>
    <style name="Base.Theme.SimpleAgenda" parent="Theme.Material3.Light.NoActionBar">
        <item name="colorPrimary">@color/green_500</item>
        <item name="colorPrimaryVariant">@color/green_700</item>
        <item name="colorSecondary">@color/teal_500</item>
        <item name="colorOnPrimary">@color/white</item>
        <item name="android:statusBarColor">@color/green_700</item>
    </style>

    <style name="Theme.SimpleAgenda" parent="Base.Theme.SimpleAgenda" />
</resources>
```

#### **themes.xml** (res/values-night/)
```xml
<resources>
    <style name="Base.Theme.SimpleAgenda" parent="Theme.Material3.Dark.NoActionBar">
        <item name="colorPrimary">@color/green_400</item>
        <item name="colorPrimaryVariant">@color/green_500</item>
        <item name="colorSecondary">@color/teal_400</item>
        <item name="colorOnPrimary">@color/black</item>
        <item name="android:statusBarColor">@color/green_700</item>
    </style>
</resources>
```

---

## 🔧 FUNCIONALIDADES REQUERIDAS

### **1. CRUD completo de Tareas (Room + ViewModel + LiveData)**
- Crear tarea
- Leer todas las tareas (ordenadas por fecha de creación descendente)
- Actualizar tarea (título, descripción, fecha, prioridad, estado done)
- Eliminar tarea (con diálogo de confirmación)
- Marcar como completada/pendiente (checkbox en el item)

### **2. CRUD completo de Notas**
- Similar a tareas pero sin campos de fecha ni prioridad

### **3. Calendario**
- Mostrar calendario del mes actual
- Al seleccionar un día, mostrar tareas con `dueAt` de ese día
- Indicador visual en días con tareas

### **4. Configuración**
- Toggle tema claro/oscuro
- Guardar preferencia en DataStore
- Aplicar tema dinámicamente sin reiniciar la app

### **5. Navegación**
- Navegación entre fragmentos con Navigation Component
- SafeArgs para pasar datos entre fragmentos
- Bottom Navigation con 4 tabs

### **6. Búsqueda y filtros (TasksFragment)**
- SearchView en el Toolbar
- Filtrar por:
  - Todas
  - Pendientes
  - Completadas
  - Por prioridad (Alta/Media/Baja)

### **7. Empty states**
- Si no hay tareas: mostrar mensaje "No hay tareas. Crea una nueva tocando +"
- Si no hay notas: mostrar mensaje "No hay notas. Crea una nueva tocando +"

---

## 📁 NAVEGACIÓN (nav_graph.xml)

```xml
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/nav_graph"
    app:startDestination="@id/tasksFragment">

    <fragment
        android:id="@+id/tasksFragment"
        android:name="com.example.simpleagenda.presentation.tasks.TasksFragment"
        android:label="Tareas">
        <action
            android:id="@+id/action_tasks_to_addEditTask"
            app:destination="@id/addEditTaskFragment" />
    </fragment>

    <fragment
        android:id="@+id/addEditTaskFragment"
        android:name="com.example.simpleagenda.presentation.tasks.AddEditTaskFragment"
        android:label="Editar Tarea">
        <argument
            android:name="taskId"
            app:argType="long"
            android:defaultValue="-1L" />
    </fragment>

    <fragment
        android:id="@+id/calendarFragment"
        android:name="com.example.simpleagenda.presentation.calendar.CalendarFragment"
        android:label="Calendario" />

    <fragment
        android:id="@+id/notesFragment"
        android:name="com.example.simpleagenda.presentation.notes.NotesFragment"
        android:label="Notas">
        <action
            android:id="@+id/action_notes_to_addEditNote"
            app:destination="@id/addEditNoteFragment" />
    </fragment>

    <fragment
        android:id="@+id/addEditNoteFragment"
        android:name="com.example.simpleagenda.presentation.notes.AddEditNoteFragment"
        android:label="Editar Nota">
        <argument
            android:name="noteId"
            app:argType="long"
            android:defaultValue="-1L" />
    </fragment>

    <fragment
        android:id="@+id/settingsFragment"
        android:name="com.example.simpleagenda.presentation.settings.SettingsFragment"
        android:label="Configuración" />

</navigation>
```

---

## 🎯 REQUISITOS IMPORTANTES

### **1. ViewBinding / DataBinding**
Usar **ViewBinding** para acceso a vistas (más seguro que findViewById)

### **2. DiffUtil en Adapters**
Implementar **DiffUtil.ItemCallback** para actualizar listas eficientemente

### **3. ViewModel + LiveData**
- Cada Fragment tiene su ViewModel
- ViewModels exponen **LiveData** (no StateFlow)
- Observar LiveData desde el Fragment:
```kotlin
viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
    adapter.submitList(tasks)
}
```

### **4. Repository Pattern**
- TaskRepository (interfaz) + TaskRepositoryImpl
- NoteRepository (interfaz) + NoteRepositoryImpl
- Los repositorios usan Room DAOs

### **5. Coroutines**
- Operaciones de Room en coroutines (`suspend fun`)
- ViewModel lanza coroutines con `viewModelScope`

### **6. Material Components**
- TextInputLayout para campos de formulario
- MaterialCardView para items de listas
- FloatingActionButton para agregar
- MaterialToolbar
- BottomNavigationView
- Chips para mostrar prioridad

### **7. Manejo de errores**
- Try/catch en operaciones de base de datos
- Mostrar Snackbar si hay error

---

## 🚫 LO QUE NO INCLUIR (simplificar)

- ❌ Firebase (solo usar Room local)
- ❌ Google Maps
- ❌ Login/Registro (no hay autenticación)
- ❌ Hilt/Dagger (inyección manual en ViewModel Factory)
- ❌ Múltiples módulos
- ❌ Testing (enfocarnos en funcionalidad)

---

## 📝 RESUMEN DE ARCHIVOS A GENERAR

### **Kotlin (35-40 archivos aprox.)**
- MainActivity.kt
- 4 Fragments principales (Tasks, Notes, Calendar, Settings)
- 2 Fragments de detalle (AddEditTask, AddEditNote)
- 4 ViewModels (Tasks, Notes, Calendar, Settings)
- 2 Adapters (TasksAdapter, NotesAdapter)
- 2 ViewHolders (TaskViewHolder, NoteViewHolder)
- 2 DAOs (TaskDao, NoteDao)
- 2 Entities (TaskEntity, NoteEntity)
- 2 Repositories (TaskRepository, NoteRepository)
- 2 Models de dominio (Task, Note)
- AppDatabase.kt
- ViewModelFactory si no usas Hilt
- Mapper/Converter de Entity ↔ Model

### **XML (20-25 archivos aprox.)**
- activity_main.xml
- fragment_tasks.xml
- fragment_notes.xml
- fragment_calendar.xml
- fragment_settings.xml
- fragment_add_edit_task.xml
- fragment_add_edit_note.xml
- item_task.xml (CardView)
- item_note.xml (CardView)
- nav_graph.xml
- bottom_nav_menu.xml
- themes.xml (values + values-night)
- colors.xml
- strings.xml
- dimens.xml (opcional)

---

## 🎨 EXTRA: DETALLES VISUALES

### **Prioridad con colores**
- Alta (3) → Chip rojo (#EF5350)
- Media (2) → Chip naranja (#FFA726)
- Baja (1) → Chip verde (#66BB6A)

### **Iconos sugeridos**
- Tareas: `@drawable/ic_task` o usar Material Icons
- Calendario: `@drawable/ic_calendar`
- Notas: `@drawable/ic_note`
- Configuración: `@drawable/ic_settings`

### **Animaciones**
- Al agregar/eliminar items en RecyclerView usar animaciones por defecto
- Opcional: ItemAnimator personalizado

---

## ✅ CHECKLIST DE ENTREGABLES

- [ ] Proyecto Android creado con package `com.example.simpleagenda`
- [ ] Dependencias configuradas (Room, Navigation, ViewBinding, etc.)
- [ ] MainActivity con BottomNavigationView funcionando
- [ ] Nav_graph con todas las rutas
- [ ] Room Database funcionando (Tasks y Notes)
- [ ] TasksFragment con RecyclerView + CRUD completo
- [ ] NotesFragment con RecyclerView + CRUD completo
- [ ] CalendarFragment con vista básica de calendario
- [ ] SettingsFragment con toggle tema claro/oscuro
- [ ] Tema verde profesional aplicado (light + dark)
- [ ] ViewModels con LiveData
- [ ] Repositorios implementados
- [ ] Adapters con DiffUtil
- [ ] Búsqueda funcionando en TasksFragment
- [ ] Filtros funcionando (Todas/Pendientes/Completadas/Prioridad)
- [ ] Diálogos de confirmación al eliminar
- [ ] Empty states en listas vacías
- [ ] App compilando sin errores
- [ ] App funcionando en emulador/dispositivo

---

## 🚀 INSTRUCCIONES PARA EL ASISTENTE

**Por favor:**

1. **Crea el proyecto completo** desde cero siguiendo esta especificación
2. **Genera todos los archivos necesarios** (Kotlin + XML)
3. **Usa arquitectura MVVM limpia** con Room
4. **Implementa RecyclerView con Adapters** correctamente
5. **Aplica Material3** con la paleta verde especificada
6. **Asegura navegación funcional** con Navigation Component
7. **Implementa ViewBinding** en todos los fragments
8. **Usa DiffUtil** en adapters
9. **Configura tema claro/oscuro** con DataStore
10. **Haz que compile y funcione** al ejecutar

**Prioriza:**
- Código limpio y bien estructurado
- Nomenclatura consistente en español (títulos UI) e inglés (código)
- Manejo de errores básico
- UX fluida con Material Components

**Entrega:**
- Código completo y funcional
- Sin errores de compilación
- Listo para ejecutar en dispositivo/emulador

---

## 📌 NOTAS FINALES

Esta app es una **versión simplificada** de la app original en Compose, pero manteniendo todas las funcionalidades core:
- Gestión de tareas y notas
- Calendario básico
- Tema personalizable
- Arquitectura MVVM
- Persistencia local con Room

**No incluye:**
- Firebase (todo es local)
- Autenticación
- Google Maps
- Funciones avanzadas

El objetivo es tener una app funcional, bien estructurada y moderna usando Views clásicas de Android.

---

## 🔥 CONFIGURACIÓN DE FIREBASE (PASO A PASO)

### **1. Copiar google-services.json**
```bash
# Desde el proyecto actual (Compose)
Origen: C:\Users\Chrome\AndroidStudioProjects\OrgaUNS\app\google-services.json

# Al nuevo proyecto (Views)
Destino: [NuevoProyecto]\app\google-services.json
```

### **2. Verificar que el package coincida**
En `build.gradle.kts (Module: app)`:
```kotlin
android {
    namespace = "com.example.orgauns"  // ⚠️ DEBE ser exactamente este
    defaultConfig {
        applicationId = "com.example.orgauns"  // ⚠️ DEBE ser exactamente este
    }
}
```

### **3. NO necesitas hacer nada más en Firebase Console**
- ✅ Ya existe el proyecto Firebase
- ✅ Ya está configurada la app con ese package
- ✅ Ya existen las colecciones users/tasks/notes
- ✅ Los datos se compartirán automáticamente

### **4. Reglas de Firestore (ya configuradas)**
Las reglas actuales permiten acceso solo a datos del usuario autenticado:
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

---

**¡Adelante, crea la app completa con Firebase!** 🚀

