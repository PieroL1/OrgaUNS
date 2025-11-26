# 📖 MANUAL TÉCNICO - OrgaUNS (Simple Agenda)

## 🎯 ¿Qué es esta app?

**OrgaUNS** es una aplicación de agenda personal para Android que permite:
- ✅ Gestionar tareas (crear, editar, completar, eliminar)
- 📅 Ver un calendario con tus tareas
- 📝 Crear y organizar notas
- 🗺️ Ver ubicaciones en un mapa
- ⚙️ Configurar tema claro/oscuro
- 🔐 Sistema de login y registro con Firebase

---

## 🏗️ ARQUITECTURA DE LA APP (Cómo está organizada)

La app usa **MVVM (Model-View-ViewModel)** con **Jetpack Compose** (interfaz moderna de Android).

```
📁 OrgaUNS/
├── 📁 data/                    → Aquí se guardan los datos
│   └── 📁 repository/          → Conectan la app con Firebase
│       ├── AuthRepositoryImpl.kt       (Login/Registro)
│       ├── TaskRepositoryImpl.kt       (Tareas)
│       ├── NoteRepositoryImpl.kt       (Notas)
│       └── SettingsRepositoryImpl.kt   (Configuración)
│
├── 📁 domain/                  → Modelos de datos y reglas de negocio
│   ├── 📁 model/               → Estructura de los datos
│   │   ├── Task.kt             (Cómo es una Tarea)
│   │   ├── Note.kt             (Cómo es una Nota)
│   │   └── User.kt             (Cómo es un Usuario)
│   ├── 📁 repository/          → Interfaces (contratos)
│   └── 📁 usecase/             → Lógica de negocio
│
├── 📁 presentation/            → Todo lo que ves en pantalla
│   ├── 📁 auth/                → Pantallas de Login y Registro
│   │   ├── LoginScreen.kt      (Pantalla de inicio de sesión)
│   │   ├── RegisterScreen.kt   (Pantalla de registro)
│   │   └── AuthViewModel.kt    (Lógica de login/registro)
│   │
│   ├── 📁 main/                → Pantalla principal con menú inferior
│   │   └── MainScreen.kt       (Contiene las 5 pestañas)
│   │
│   ├── 📁 tasks/               → Pantalla de Tareas
│   │   ├── TasksScreen.kt      (Interfaz visual)
│   │   └── TasksViewModel.kt   (Lógica de tareas)
│   │
│   ├── 📁 calendar/            → Pantalla de Calendario
│   │   ├── CalendarScreen.kt   (Interfaz visual)
│   │   └── CalendarViewModel.kt (Lógica del calendario)
│   │
│   ├── 📁 notes/               → Pantalla de Notas
│   │   ├── NotesScreen.kt      (Interfaz visual)
│   │   └── NotesViewModel.kt   (Lógica de notas)
│   │
│   ├── 📁 map/                 → Pantalla del Mapa
│   │   └── MapScreen.kt        (Google Maps)
│   │
│   ├── 📁 settings/            → Pantalla de Configuración
│   │   ├── SettingsScreen.kt   (Interfaz visual)
│   │   └── SettingsViewModel.kt (Lógica de ajustes)
│   │
│   ├── 📁 splash/              → Pantalla de inicio (logo)
│   │   └── SplashScreen.kt     (Se muestra 2 segundos al abrir)
│   │
│   └── 📁 navigation/          → Navegación entre pantallas
│       ├── AppNavigation.kt    (Define todas las rutas)
│       └── Route.kt            (Nombres de las rutas)
│
├── 📁 ui/theme/                → Colores y estilos visuales
│   ├── Color.kt                (Paleta verde profesional)
│   ├── Theme.kt                (Tema claro/oscuro)
│   └── Type.kt                 (Tipografía)
│
└── MainActivity.kt             → Punto de entrada de la app
```

---

## 🚀 FLUJO COMPLETO DE LA APP (Paso a paso)

### **1. Inicio de la App (MainActivity.kt)**

**¿Qué hace?**
- Es la **puerta de entrada** de toda la app.
- Carga el tema (claro u oscuro) desde la configuración guardada.
- Inicia el sistema de navegación.

**Código simplificado:**
```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Aquí se carga la app completa
            OrgaUNSApp()
        }
    }
}
```

**¿Qué pasa después?**
→ Va a `AppNavigation.kt`

---

### **2. Navegación (AppNavigation.kt)**

**¿Qué hace?**
- Define **todas las pantallas** de la app.
- Decide **qué pantalla mostrar primero**.
- Controla cómo el usuario **se mueve entre pantallas**.

**Flujo de pantallas:**
```
📱 INICIO
  ↓
🟢 SplashScreen (Pantalla de logo - 2 segundos)
  ↓
  ├─➡️ ¿Usuario ya logueado? → MainScreen (Pantalla principal)
  └─➡️ ¿Usuario NO logueado? → LoginScreen (Pantalla de login)
```

**Código simplificado:**
```kotlin
NavHost(
    navController = navController,
    startDestination = Route.Splash.route  // ¡Siempre empieza aquí!
) {
    // 1. Splash Screen (logo)
    composable(Route.Splash.route) {
        SplashScreen(
            onNavigateToLogin = { /* Ir a Login */ },
            onNavigateToMain = { /* Ir a MainScreen */ }
        )
    }

    // 2. Login y Registro
    navigation(route = Route.Auth.route) {
        composable(Route.Login.route) {
            LoginScreen(
                onNavigateToRegister = { /* Ir a Registro */ },
                onLoginSuccess = { /* Ir a MainScreen */ }
            )
        }
        
        composable(Route.Register.route) {
            RegisterScreen(
                onNavigateToLogin = { /* Volver a Login */ },
                onRegisterSuccess = { /* Ir a MainScreen */ }
            )
        }
    }

    // 3. Pantalla Principal
    composable(Route.Main.route) {
        MainScreen(
            onLogout = { /* Volver a Login */ }
        )
    }
}
```

---

### **3. Pantalla de Login (LoginScreen.kt)**

**¿Qué muestra?**
- 📧 Campo para Email
- 🔒 Campo para Contraseña
- 🟢 Botón "Iniciar Sesión"
- 🔗 Link "¿No tienes cuenta? Regístrate"

**¿Qué hace internamente?**
```kotlin
@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(),  // ← Aquí está la LÓGICA
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    // 1. Guardar lo que el usuario escribe
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    // 2. Observar el estado del ViewModel
    val uiState by viewModel.uiState.collectAsState()
    
    // 3. Si el login es exitoso, ir a MainScreen
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onLoginSuccess()  // ← Navega a MainScreen
        }
    }
    
    // 4. Botón de Login
    Button(
        onClick = { 
            viewModel.signIn(email, password)  // ← Llama al ViewModel
        }
    ) {
        Text("Iniciar Sesión")
    }
}
```

**Flujo:**
```
Usuario escribe email/password
  ↓
Presiona "Iniciar Sesión"
  ↓
Llama a AuthViewModel.signIn()
  ↓
AuthViewModel llama a AuthRepository
  ↓
AuthRepository se conecta a Firebase
  ↓
¿Login exitoso? → LoginScreen detecta éxito → Navega a MainScreen
¿Error? → Muestra mensaje de error en pantalla
```

---

### **4. Lógica de Login (AuthViewModel.kt)**

**¿Qué hace?**
- Conecta la **interfaz visual** (LoginScreen) con los **datos** (Firebase).
- Gestiona los **estados**: cargando, éxito, error.

**Código simplificado:**
```kotlin
class AuthViewModel : ViewModel() {
    // Estado observable que LoginScreen vigila
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    // Función que se llama al presionar "Iniciar Sesión"
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            // 1. Mostrar "Cargando..."
            _uiState.value = AuthUiState(isLoading = true)
            
            // 2. Intentar hacer login en Firebase
            when (val result = authRepository.signIn(email, password)) {
                is AuthResult.Success -> {
                    // Login exitoso
                    _uiState.value = AuthUiState(isSuccess = true)
                }
                is AuthResult.Error -> {
                    // Error (ej: contraseña incorrecta)
                    _uiState.value = AuthUiState(error = result.message)
                }
            }
        }
    }
}
```

**Estados posibles:**
```
AuthUiState(
    isLoading = true,    → Muestra un spinner de carga
    isSuccess = true,    → Navega a MainScreen
    error = "Mensaje"    → Muestra error en pantalla
)
```

---

### **5. Conexión con Firebase (AuthRepositoryImpl.kt)**

**¿Qué hace?**
- Se comunica directamente con **Firebase Authentication**.
- Crea usuarios nuevos o verifica credenciales.

**Código simplificado:**
```kotlin
class AuthRepositoryImpl : AuthRepository {
    private val auth = Firebase.auth  // Firebase Authentication
    
    override suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            // Intentar login con Firebase
            auth.signInWithEmailAndPassword(email, password).await()
            AuthResult.Success(auth.currentUser)
        } catch (e: Exception) {
            // Si falla, devolver error
            AuthResult.Error("Email o contraseña incorrectos")
        }
    }
    
    override suspend fun signUp(email: String, password: String): AuthResult {
        return try {
            // Crear nueva cuenta en Firebase
            auth.createUserWithEmailAndPassword(email, password).await()
            AuthResult.Success(auth.currentUser)
        } catch (e: Exception) {
            AuthResult.Error("Error al crear cuenta")
        }
    }
}
```

---

### **6. Registro de Usuario (RegisterScreen.kt)**

**Similar a LoginScreen pero:**
- Pide **confirmar contraseña**.
- Llama a `viewModel.signUp()` en lugar de `signIn()`.
- Muestra link "¿Ya tienes cuenta? Inicia sesión".

**Flujo:**
```
Usuario ingresa email, password, confirmar password
  ↓
Presiona "Registrarse"
  ↓
AuthViewModel.signUp()
  ↓
AuthRepository crea cuenta en Firebase
  ↓
¿Éxito? → Navega a MainScreen
¿Error? → Muestra error
```

---

### **7. Pantalla Principal (MainScreen.kt)**

**¿Qué muestra?**
- **Barra inferior** con 5 pestañas:
  1. 📋 Tareas
  2. 📅 Calendario
  3. 📝 Notas
  4. 🗺️ Mapa
  5. ⚙️ Configuración

**Código simplificado:**
```kotlin
@Composable
fun MainScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()
    var selectedItem by remember { mutableStateOf(0) }  // Pestaña seleccionada
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                // 5 botones de navegación
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            navController.navigate(item.route)
                        },
                        icon = { Icon(item.icon) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) {
        // Contenido según pestaña seleccionada
        NavHost(navController, startDestination = Route.Tasks.route) {
            composable(Route.Tasks.route) { TasksScreen() }
            composable(Route.Calendar.route) { CalendarScreen() }
            composable(Route.Notes.route) { NotesScreen() }
            composable(Route.Map.route) { MapScreen() }
            composable(Route.Settings.route) { SettingsScreen(onLogout) }
        }
    }
}
```

---

### **8. Pantalla de Tareas (TasksScreen.kt + TasksViewModel.kt)**

**¿Qué muestra?**
- Lista de tareas del usuario.
- Botón "+" para agregar nueva tarea.
- Cada tarea muestra:
  - Título
  - Descripción
  - Prioridad (Alta/Media/Baja con colores)
  - Checkbox para marcar como completada
  - Botón para eliminar

**¿Cómo funciona?**

**TasksScreen.kt (Interfaz visual):**
```kotlin
@Composable
fun TasksScreen(viewModel: TasksViewModel = viewModel()) {
    // 1. Observar las tareas desde Firebase
    val tasks by viewModel.tasks.collectAsState()
    
    // 2. Mostrar lista
    LazyColumn {
        items(tasks) { task →
            TaskCard(
                task = task,
                onToggle = { viewModel.toggleTaskDone(task.id) },
                onDelete = { viewModel.deleteTask(task.id) }
            )
        }
    }
    
    // 3. Botón para agregar
    FloatingActionButton(onClick = { viewModel.addTask(...) }) {
        Icon(Icons.Default.Add)
    }
}
```

**TasksViewModel.kt (Lógica):**
```kotlin
class TasksViewModel : ViewModel() {
    // Lista de tareas observable
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()
    
    init {
        // Cargar tareas al iniciar
        loadTasks()
    }
    
    private fun loadTasks() {
        viewModelScope.launch {
            // Obtener tareas de Firebase
            taskRepository.getTasks().collect { taskList →
                _tasks.value = taskList
            }
        }
    }
    
    fun addTask(title: String, description: String, priority: Int) {
        viewModelScope.launch {
            taskRepository.addTask(Task(title, description, priority))
        }
    }
    
    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            taskRepository.deleteTask(taskId)
        }
    }
}
```

**¿Dónde se guardan las tareas?**
→ En **Firebase Firestore** (base de datos en la nube).

**Estructura en Firebase:**
```
Firestore Database:
  └── users/
      └── [userId]/
          └── tasks/
              ├── task1: { title, description, priority, done, ... }
              ├── task2: { ... }
              └── task3: { ... }
```

---

### **9. Pantalla de Configuración (SettingsScreen.kt)**

**¿Qué muestra?**
- Toggle para cambiar entre **tema claro/oscuro**.
- Botón de **Cerrar Sesión**.
- Información de la versión de la app.

**Código simplificado:**
```kotlin
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    onLogout: () -> Unit
) {
    val isDarkMode by viewModel.isDarkMode.collectAsState(initial = false)
    
    Column {
        // Switch de tema
        SwitchSetting(
            title = "Modo Oscuro",
            checked = isDarkMode,
            onCheckedChange = { viewModel.toggleDarkMode(it) }
        )
        
        // Botón de logout
        Button(onClick = {
            viewModel.signOut()
            onLogout()  // ← Navega de vuelta a LoginScreen
        }) {
            Text("Cerrar Sesión")
        }
    }
}
```

**¿Cómo se guarda la preferencia del tema?**
→ En **DataStore** (almacenamiento local del dispositivo).

---

## 🔄 RESUMEN DEL FLUJO COMPLETO

```
1️⃣ Usuario abre la app
   ↓
2️⃣ MainActivity carga
   ↓
3️⃣ AppNavigation inicia en SplashScreen
   ↓
4️⃣ SplashScreen verifica si hay usuario logueado
   ├─➡️ SÍ → MainScreen (Pantalla principal)
   └─➡️ NO → LoginScreen
   
5️⃣ En LoginScreen:
   Usuario ingresa email/password → Presiona "Iniciar Sesión"
   ↓
   AuthViewModel.signIn(email, password)
   ↓
   AuthRepository conecta con Firebase
   ↓
   Firebase valida credenciales
   ├─➡️ ✅ Éxito → Navega a MainScreen
   └─➡️ ❌ Error → Muestra mensaje de error
   
6️⃣ En MainScreen:
   Usuario puede navegar entre 5 pestañas
   
7️⃣ En TasksScreen (pestaña de Tareas):
   TasksViewModel carga tareas desde Firebase
   ↓
   Muestra lista en pantalla
   ↓
   Usuario puede:
   - Agregar tarea → TasksViewModel.addTask() → Firebase
   - Marcar completada → TasksViewModel.toggleTaskDone() → Firebase
   - Eliminar tarea → TasksViewModel.deleteTask() → Firebase
   
8️⃣ En SettingsScreen:
   Usuario puede:
   - Cambiar tema → SettingsViewModel.toggleDarkMode() → DataStore
   - Cerrar sesión → AuthRepository.signOut() → Navega a LoginScreen
```

---

## 🧩 CONCEPTOS CLAVE EXPLICADOS

### **1. ¿Qué es un ViewModel?**
Es la **caja de lógica** de cada pantalla. 
- **NO sabe nada** de la interfaz visual.
- Conecta la pantalla con los datos (Firebase, DataStore).
- Guarda el **estado** de la pantalla (ej: lista de tareas, loading, errores).

**Ejemplo:**
```
TasksScreen (lo que ves) ←→ TasksViewModel (la lógica) ←→ TaskRepository (Firebase)
```

### **2. ¿Qué es un Repository?**
Es la **conexión con Firebase**.
- TaskRepository → Gestiona tareas en Firestore.
- AuthRepository → Gestiona login/registro en Firebase Auth.
- NoteRepository → Gestiona notas en Firestore.

### **3. ¿Qué es StateFlow / LiveData?**
Es un **dato observable**.
- Cuando el dato cambia, la pantalla se **actualiza automáticamente**.

**Ejemplo:**
```kotlin
// En ViewModel:
val tasks = MutableStateFlow<List<Task>>(emptyList())

// En Screen:
val tasks by viewModel.tasks.collectAsState()
// ↑ Cada vez que 'tasks' cambie en el ViewModel, la pantalla se redibuja
```

### **4. ¿Qué es Jetpack Compose?**
Es la forma **moderna** de crear interfaces en Android.
- NO usa XML (como la versión clásica).
- Todo se escribe en código Kotlin con **funciones @Composable**.

**Ejemplo:**
```kotlin
@Composable  // ← Esta anotación indica que es una función de UI
fun MiBoton() {
    Button(onClick = { /* hacer algo */ }) {
        Text("Presióname")
    }
}
```

### **5. ¿Qué es Firebase?**
Es la **nube de Google** donde se guardan:
- **Usuarios** (Firebase Authentication).
- **Tareas y Notas** (Firestore Database).

**Ventaja:** Los datos están en internet, puedes acceder desde cualquier dispositivo.

---

## 📂 ARCHIVOS IMPORTANTES

| Archivo | ¿Qué hace? |
|---------|-----------|
| `MainActivity.kt` | Punto de entrada de la app |
| `AppNavigation.kt` | Define todas las pantallas y rutas |
| `LoginScreen.kt` | Interfaz visual del login |
| `AuthViewModel.kt` | Lógica de login/registro |
| `AuthRepositoryImpl.kt` | Conexión con Firebase Auth |
| `MainScreen.kt` | Pantalla principal con menú inferior |
| `TasksScreen.kt` | Interfaz visual de tareas |
| `TasksViewModel.kt` | Lógica de gestión de tareas |
| `TaskRepositoryImpl.kt` | Conexión con Firebase Firestore (tareas) |
| `SettingsScreen.kt` | Interfaz de configuración |
| `SettingsViewModel.kt` | Lógica de tema claro/oscuro |
| `Theme.kt` | Colores y estilos de la app |
| `google-services.json` | Configuración de Firebase |

---

## 🎨 TEMA Y COLORES

La app usa una **paleta verde profesional**:

**Colores principales:**
- 🟢 Verde primario: `#43A047`
- 🟢 Verde secundario: `#66BB6A`
- 🟢 Verde oscuro: `#388E3C`

**Prioridades en tareas:**
- 🔴 Alta: Rojo `#EF5350`
- 🟠 Media: Naranja `#FFA726`
- 🟢 Baja: Verde `#66BB6A`

**Tema claro vs oscuro:**
- Se guarda en **DataStore** (almacenamiento local).
- Se aplica dinámicamente sin reiniciar la app.

---

## 🔧 TECNOLOGÍAS USADAS

| Tecnología | ¿Para qué? |
|------------|-----------|
| **Kotlin** | Lenguaje de programación |
| **Jetpack Compose** | Crear interfaces modernas |
| **Firebase Auth** | Login y registro de usuarios |
| **Firestore** | Base de datos en la nube (tareas/notas) |
| **DataStore** | Guardar configuración local (tema) |
| **Navigation Compose** | Navegación entre pantallas |
| **StateFlow** | Datos observables (actualización automática) |
| **Coroutines** | Operaciones asíncronas (no bloquean la app) |
| **Google Maps** | Mostrar mapas en la pestaña "Mapa" |

---

## 🐛 CÓMO PROBAR LA APP

### **1. Abrir el proyecto en Android Studio**
- Abre Android Studio.
- File → Open → Selecciona la carpeta `OrgaUNS`.

### **2. Ejecutar la app**
- Conecta un dispositivo Android o abre un emulador.
- Presiona el botón ▶️ (Run).

### **3. Flujo de prueba**
```
1. Se abre SplashScreen (logo) por 2 segundos
2. Aparece LoginScreen
3. Presiona "¿No tienes cuenta? Regístrate"
4. Crea una cuenta con email/password
5. Te lleva automáticamente a MainScreen
6. Navega entre las 5 pestañas:
   - Tareas: Crea, completa, elimina tareas
   - Calendario: Ve tus tareas por fecha
   - Notas: Crea y edita notas
   - Mapa: Explora ubicaciones
   - Configuración: Cambia tema y cierra sesión
```

---

## ❓ PREGUNTAS FRECUENTES

### **¿Dónde se guardan las tareas?**
→ En **Firebase Firestore** (base de datos en la nube de Google).

### **¿Dónde se guarda el tema claro/oscuro?**
→ En **DataStore** (almacenamiento local del dispositivo).

### **¿Qué pasa si elimino la app?**
→ Las tareas/notas siguen en Firebase, pero la preferencia de tema se pierde.

### **¿Puedo usar la app sin internet?**
→ NO, necesitas internet porque usa Firebase.

### **¿Cómo agrego una nueva pantalla?**
1. Crea un archivo `MiPantallaScreen.kt` en `presentation/`.
2. Agrega la ruta en `Route.kt`.
3. Agrega el `composable()` en `AppNavigation.kt`.

### **¿Cómo cambio los colores?**
→ Edita `Color.kt` en `ui/theme/`.

---

## 📚 GLOSARIO

| Término | Significado |
|---------|-------------|
| **Composable** | Función que dibuja interfaz en Jetpack Compose |
| **ViewModel** | Clase que guarda la lógica de una pantalla |
| **Repository** | Clase que se conecta con Firebase |
| **StateFlow** | Dato observable que actualiza automáticamente la UI |
| **NavController** | Controla la navegación entre pantallas |
| **Firebase Auth** | Servicio de autenticación de Google |
| **Firestore** | Base de datos NoSQL en la nube de Google |
| **DataStore** | Sistema de almacenamiento local de Android |
| **Coroutine** | Código que se ejecuta en segundo plano sin bloquear la app |

---

## 🎯 CONCLUSIÓN

**OrgaUNS** es una app MVVM moderna que:
- ✅ Separa **interfaz** (Screen) de **lógica** (ViewModel) de **datos** (Repository).
- ✅ Usa **Firebase** para guardar datos en la nube.
- ✅ Usa **Jetpack Compose** para interfaces modernas.
- ✅ Tiene **navegación** entre pantallas con Navigation Component.
- ✅ Soporta **tema claro/oscuro** con DataStore.

**Flujo clave:**
```
Screen → ViewModel → Repository → Firebase
```

**Todo está conectado y se actualiza automáticamente gracias a StateFlow.**

---

📌 **Si tienes dudas sobre alguna pantalla o archivo específico, pregunta y te explico con más detalle.**

