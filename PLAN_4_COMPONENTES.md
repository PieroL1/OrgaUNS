# 📋 PLAN: Implementar los 4 Componentes de Android en OrgaUNS

## ✅ Estado Actual
- **Activity**: ✅ Ya implementado (MainActivity)
- **Service**: ❌ Falta implementar
- **BroadcastReceiver**: ❌ Falta implementar
- **ContentProvider**: ❌ Falta implementar

---

## 🎯 PLAN DE IMPLEMENTACIÓN (Paso a Paso)

### 📌 FASE 1: SERVICE (Servicio de Sincronización) ⭐ MEJORADO

#### ¿Qué vamos a hacer?
Crear un **servicio en segundo plano** con múltiples funcionalidades VISIBLES y DEMOSTRABLES.

#### ¿Qué componentes crear?
1. **TaskSyncService.kt** - El servicio que sincroniza tareas
2. **WorkManager** - Para programar la sincronización cada X horas
3. **Botón "Sincronizar Ahora"** en la pantalla de Ajustes - PARA DEMOSTRAR AL INSTANTE
4. Registrar el servicio en **AndroidManifest.xml**

#### ¿Qué hace exactamente? (FUNCIONALIDADES VISIBLES)

**✅ FÁCIL DE DEMOSTRAR:**
1. **Botón manual de sincronización** - Agregaremos un botón en la pantalla de Ajustes que dice "Sincronizar Ahora"
   - Presionas el botón
   - Muestra una notificación "Sincronizando..."
   - Después muestra "Sincronización completada: X tareas actualizadas"
   - **El profesor lo ve al instante** ✨

2. **Notificación visible cada vez que sincroniza**
   - Muestra cuántas tareas se sincronizaron
   - Muestra la hora de la última sincronización
   - **Evidencia visual clara** ✨

3. **Indicador de estado en la app**
   - En Ajustes verás: "Última sincronización: hace 5 minutos"
   - Muestra si está sincronizando en este momento
   - **Feedback inmediato** ✨

**⏰ AUTOMÁTICO (Funciona solo):**
4. Sincronización cada 6 horas en segundo plano
5. Sincroniza al abrir la app

#### Archivos a crear:
- `app/src/main/java/com/example/orgauns/service/TaskSyncService.kt`
- `app/src/main/java/com/example/orgauns/service/SyncWorker.kt`
- Modificar **SettingsScreen.kt** para agregar el botón de sincronización manual

#### Cambios necesarios:
- Agregar dependencia de WorkManager en `build.gradle.kts`
- Agregar permiso de notificaciones en `AndroidManifest.xml`
- Registrar el servicio en `AndroidManifest.xml`
- Agregar botón en pantalla de Ajustes

#### 🎯 CÓMO DEMOSTRARLO AL PROFESOR:
1. Abre la app y ve a "Ajustes"
2. Presiona el botón "Sincronizar Ahora"
3. Muestra la notificación que aparece
4. Muestra el indicador de "Última sincronización"
5. **¡Listo! El profesor ve que el Service funciona** ✅

---

### 📌 FASE 2: BROADCASTRECEIVER (Receptor de Eventos) ⭐ MEJORADO

#### ¿Qué vamos a hacer?
Crear **receptores** con funcionalidades INMEDIATAS y FÁCILES de demostrar en tiempo real.

#### ¿Qué componentes crear?
1. **TaskReminderReceiver.kt** - Receptor para alarmas/recordatorios de tareas
2. **BootCompletedReceiver.kt** - Receptor que escucha el reinicio del sistema
3. **BatteryLowReceiver.kt** - **NUEVO** Receptor que detecta batería baja (FÁCIL DE DEMOSTRAR)
4. **AlarmScheduler.kt** - Clase helper para programar alarmas
5. **NotificationHelper.kt** - Helper para mostrar notificaciones

#### ¿Qué hace exactamente? (FUNCIONALIDADES VISIBLES)

**✅ SÚPER FÁCIL DE DEMOSTRAR (1 minuto):**

**1. TaskReminderReceiver - Recordatorio INMEDIATO:**
   - Creas una tarea con recordatorio en 1 minuto
   - Esperas 1 minuto
   - **¡BOOM! Aparece notificación** 🔔
   - La notificación tiene botones: "Completar" y "Posponer"
   - El profesor ve la notificación en tiempo real
   - **MUY FÁCIL DE DEMOSTRAR** ✨

**2. BatteryLowReceiver - Demostración INSTANTÁNEA:**
   - Cuando la batería baja del 15%, muestra notificación
   - Mensaje: "Batería baja detectada. Guardando tus tareas..."
   - Simula un auto-guardado de seguridad
   - **Puedes simular esto con comandos ADB** (te enseño cómo)
   - **DEMOSTRACIÓN EN 10 SEGUNDOS** ✨

**3. Botón de prueba en Ajustes:**
   - Agregaremos un botón "Probar Recordatorio de Prueba"
   - Al presionarlo, programa una notificación en 10 segundos
   - **¡El profesor lo ve sin esperar!** ✨

**⏰ FUNCIONALIDADES ADICIONALES:**

**4. BootCompletedReceiver:**
   - Se activa cuando el teléfono se enciende
   - Re-programa todas las alarmas de recordatorios de tareas
   - Muestra notificación: "OrgaUNS: X recordatorios reprogramados"
   - Verifica si hay tareas vencidas y muestra notificación

#### Archivos a crear:
- `app/src/main/java/com/example/orgauns/receiver/TaskReminderReceiver.kt`
- `app/src/main/java/com/example/orgauns/receiver/BootCompletedReceiver.kt`
- `app/src/main/java/com/example/orgauns/receiver/BatteryLowReceiver.kt` ⭐ NUEVO
- `app/src/main/java/com/example/orgauns/utils/AlarmScheduler.kt`
- `app/src/main/java/com/example/orgauns/utils/NotificationHelper.kt`
- Modificar **SettingsScreen.kt** para agregar botón de prueba

#### Cambios necesarios:
- Agregar permiso `RECEIVE_BOOT_COMPLETED` en `AndroidManifest.xml`
- Agregar permiso `SCHEDULE_EXACT_ALARM` en `AndroidManifest.xml`
- Agregar permiso `POST_NOTIFICATIONS` en `AndroidManifest.xml`
- Registrar los 3 receivers en `AndroidManifest.xml`
- Agregar canal de notificaciones
- Agregar botón de prueba en Ajustes

#### 🎯 CÓMO DEMOSTRARLO AL PROFESOR (3 opciones):

**OPCIÓN 1 - LA MÁS RÁPIDA (10 segundos):**
1. Ve a Ajustes
2. Presiona "Probar Notificación de Recordatorio"
3. Espera 10 segundos
4. **¡Aparece la notificación!** ✅

**OPCIÓN 2 - MÁS REALISTA (1 minuto):**
1. Crea una tarea nueva
2. Ponle fecha de hoy y hora dentro de 1 minuto
3. Activa el recordatorio
4. Espera 1 minuto
5. **¡Aparece la notificación!** ✅

**OPCIÓN 3 - Batería baja (con comando ADB):**
1. Conecta el teléfono a la PC
2. Ejecuta: `adb shell dumpsys battery set level 10`
3. **¡Aparece la notificación de batería baja!** ✅
4. Resetea: `adb shell dumpsys battery reset`

---

### 📌 FASE 3: CONTENTPROVIDER (Proveedor de Contenido) ⭐ MEJORADO

#### ¿Qué vamos a hacer?
Crear un **proveedor de contenido** con una **app de prueba visible** que demuestre que funciona.

#### ¿Qué componentes crear?
1. **TasksContentProvider.kt** - Proveedor que expone las tareas
2. **TasksContract.kt** - Contrato/esquema de cómo acceder a los datos
3. **Pantalla de Prueba DENTRO de tu app** - ⭐ NUEVA y FÁCIL DE DEMOSTRAR

#### ¿Qué hace exactamente? (FUNCIONALIDADES VISIBLES)

**✅ SÚPER FÁCIL DE DEMOSTRAR:**

**1. Pantalla de Prueba del ContentProvider (DENTRO de tu app):**
   - Nueva pantalla accesible desde Ajustes: "Probar ContentProvider"
   - Muestra un botón: "Leer Tareas vía ContentProvider"
   - Al presionarlo:
     - Consulta las tareas usando la URI del ContentProvider
     - Muestra el resultado: "✅ ContentProvider funcionando: X tareas leídas"
     - Lista las primeras 3 tareas obtenidas
   - **El profesor ve que funciona EN TU PROPIA APP** ✨
   - **NO necesitas otra app externa** ✨

**2. Widget en la pantalla principal del teléfono:**
   - Widget que muestra tus 3 tareas más urgentes
   - Actualiza automáticamente desde el ContentProvider
   - Se puede agregar a la pantalla principal (long press → Widgets)
   - **MUY VISUAL** ✨

**3. Log visible en pantalla:**
   - Cuando otras apps (o tu pantalla de prueba) consultan el ContentProvider
   - Muestra en tiempo real: "ContentProvider consultado: 5 tareas devueltas"
   - Contador de consultas en Ajustes
   - **Evidencia de que funciona** ✨

#### ¿Cómo funciona técnicamente?
- Permite que otras apps lean las tareas (solo lectura por seguridad)
- Usa URIs tipo: `content://com.example.orgauns.provider/tasks`
- Tu propia app también puede usarlo (lo demostraremos así)
- Puede alimentar widgets de Android

#### Archivos a crear:
- `app/src/main/java/com/example/orgauns/provider/TasksContentProvider.kt`
- `app/src/main/java/com/example/orgauns/provider/TasksContract.kt`
- `app/src/main/java/com/example/orgauns/presentation/settings/ContentProviderTestScreen.kt` ⭐ NUEVA
- `app/src/main/java/com/example/orgauns/widget/TaskWidget.kt` (opcional pero recomendado)
- `app/src/main/res/xml/widget_info.xml` (metadata del widget)
- `app/src/main/res/layout/widget_layout.xml` (diseño del widget)

#### Cambios necesarios:
- Registrar el provider en `AndroidManifest.xml`
- Definir las URIs y permisos de acceso
- Agregar navegación a la pantalla de prueba desde Ajustes
- Si haces widget: agregar metadata y layouts XML

#### 🎯 CÓMO DEMOSTRARLO AL PROFESOR (2 opciones):

**OPCIÓN 1 - LA MÁS FÁCIL (30 segundos):**
1. Abre la app
2. Ve a Ajustes
3. Presiona "Probar ContentProvider"
4. Presiona "Leer Tareas vía ContentProvider"
5. **¡Muestra las tareas leídas con éxito!** ✅
6. Muestra el contador de consultas

**OPCIÓN 2 - CON WIDGET (más impresionante):**
1. Mantén presionada la pantalla principal del teléfono
2. Busca "Widgets"
3. Arrastra el widget "OrgaUNS Tareas"
4. **¡El widget muestra tus tareas!** ✅
5. Explica que el widget usa el ContentProvider para obtener los datos

---

## 📊 RESUMEN DE IMPLEMENTACIÓN

### Total de archivos nuevos a crear: ~12-14 archivos

| Componente | Archivos | Complejidad | Utilidad | Demostración |
|------------|----------|-------------|----------|--------------|
| **Service** | 2 archivos + modificar Ajustes | ⭐⭐ Media | Alta - Sincroniza en segundo plano | ✅ Botón instantáneo |
| **BroadcastReceiver** | 5 archivos + modificar Ajustes | ⭐⭐ Media | Alta - Recordatorios y eventos | ✅ Notificación en 10 seg |
| **ContentProvider** | 6 archivos (con widget) | ⭐⭐⭐ Media-Alta | Alta - Widget + pantalla prueba | ✅ Pantalla de prueba interna |

---

## 🚀 ORDEN RECOMENDADO DE IMPLEMENTACIÓN

### Paso 1: BroadcastReceiver (Más fácil y útil)
- Empezar con recordatorios de tareas
- Luego agregar el receptor de reinicio
- **Tiempo estimado:** 1-2 horas

### Paso 2: Service (Medio)
- Crear servicio de sincronización básico
- Configurar WorkManager
- **Tiempo estimado:** 2-3 horas

### Paso 3: ContentProvider (Más complejo)
- Crear el proveedor de contenido
- Opcionalmente crear widget
- **Tiempo estimado:** 3-4 horas

---

## 📝 DEPENDENCIAS NECESARIAS

Agregar en `app/build.gradle.kts`:

```kotlin
// WorkManager para Service
implementation("androidx.work:work-runtime-ktx:2.9.0")

// AlarmManager ya viene incluido en Android
// ContentProvider ya viene incluido en Android
```

---

## 🎓 EXPLICACIÓN SIMPLE PARA EL PROFESOR

### Service (Servicio)
"Implementé un servicio que sincroniza las tareas con Firebase en segundo plano cada 6 horas usando WorkManager, garantizando que los datos estén actualizados incluso cuando la app está cerrada."

### BroadcastReceiver (Receptor)
"Implementé dos receptores: uno que se activa al reiniciar el teléfono para reprogramar alarmas, y otro que muestra notificaciones recordando tareas pendientes a la hora programada."

### ContentProvider (Proveedor)
"Implementé un proveedor de contenido que expone las tareas mediante URIs, permitiendo que otras aplicaciones o widgets accedan a la información de manera segura y controlada."

---

## ✅ CHECKLIST DE IMPLEMENTACIÓN

### Service
- [ ] Crear TaskSyncService.kt
- [ ] Crear SyncWorker.kt
- [ ] Agregar dependencia WorkManager
- [ ] Registrar servicio en AndroidManifest.xml
- [ ] Probar sincronización manual
- [ ] Programar sincronización periódica

### BroadcastReceiver
- [ ] Crear TaskReminderReceiver.kt
- [ ] Crear BootCompletedReceiver.kt
- [ ] Crear BatteryLowReceiver.kt ⭐ NUEVO
- [ ] Crear AlarmScheduler.kt
- [ ] Crear NotificationHelper.kt
- [ ] Agregar permisos en AndroidManifest.xml
- [ ] Registrar los 3 receivers en AndroidManifest.xml
- [ ] Crear canal de notificaciones
- [ ] Agregar botón de prueba en Ajustes
- [ ] Probar recordatorio de tarea (1 minuto)
- [ ] Probar botón de prueba instantánea (10 segundos)
- [ ] Probar reinicio de teléfono (opcional)
- [ ] Probar BatteryLowReceiver con ADB (opcional)

### ContentProvider
- [ ] Crear TasksContract.kt
- [ ] Crear TasksContentProvider.kt
- [ ] Implementar métodos query, insert, update, delete
- [ ] Registrar provider en AndroidManifest.xml
- [ ] Crear ContentProviderTestScreen.kt ⭐ PANTALLA DE PRUEBA
- [ ] Agregar navegación desde Ajustes a pantalla de prueba
- [ ] Crear TaskWidget.kt (opcional pero recomendado)
- [ ] Probar consultas con URI desde la pantalla de prueba
- [ ] Probar widget (si se implementa)

---

## 🎯 RESULTADO FINAL

Al terminar, tu app tendrá:
1. ✅ **1 Activity** - MainActivity (ya existe)
2. ✅ **1 Service** - TaskSyncService con WorkManager
3. ✅ **3 BroadcastReceivers** - TaskReminderReceiver + BootCompletedReceiver + BatteryLowReceiver
4. ✅ **1 ContentProvider** - TasksContentProvider (+ Widget opcional)

**TOTAL: Los 4 componentes fundamentales de Android implementados** ✨

### 🎬 TODAS las funcionalidades son DEMOSTRABLES AL INSTANTE:
- **Service**: Presiona botón "Sincronizar Ahora" → Notificación inmediata ✅
- **BroadcastReceiver**: Presiona botón "Probar Recordatorio" → Notificación en 10 segundos ✅
- **ContentProvider**: Presiona botón "Leer vía Provider" → Muestra tareas al instante ✅

---

## 💡 NOTAS IMPORTANTES

- Todos los componentes son **compatibles con Jetpack Compose**
- No hay que tocar las pantallas existentes (Composables)
- Los componentes funcionan "por detrás" de la UI
- Cada componente agrega funcionalidad real y útil a la app
- El profesor podrá ver los componentes registrados en el **AndroidManifest.xml**

---

## 📞 ¿DUDAS?

Si algo no queda claro de este plan, pregunta antes de empezar a implementar. Es mejor entender bien cada fase antes de codificar.

**¿Listo para empezar? Dime por cuál componente quieres comenzar y vamos paso a paso.**

