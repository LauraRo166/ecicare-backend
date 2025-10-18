# Colección de Postman - EciCare Challenges & Modules API

## 📋 Descripción

Esta colección de Postman contiene todos los endpoints para probar la funcionalidad de **Challenges** y **Modules** del backend de EciCare, con énfasis especial en las pruebas de **borrado en cascada** (cuando se elimina un módulo, se eliminan automáticamente todos los challenges asociados).

## 🚀 Importar la Colección

1. Abre Postman
2. Click en **Import** (botón superior izquierdo)
3. Selecciona el archivo `EciCare-Challenges.postman_collection.json`
4. La colección se importará con todas las variables configuradas

## 🔧 Variables de Colección

La colección incluye las siguientes variables que puedes modificar fácilmente:

| Variable | Valor por Defecto | Descripción |
|----------|-------------------|-------------|
| `base_url` | `http://localhost:8080/api` | URL base del API (cambia según tu entorno) |
| `test_module_name` | `test-module-cascade` | Nombre del módulo de prueba |
| `test_challenge_name` | `test-challenge-example` | Nombre del challenge de prueba |
| `test_user_email` | `test@example.com` | Email del usuario de prueba |

### Cómo Cambiar las Variables

1. Click derecho en la colección "EciCare - Challenges & Modules API"
2. Selecciona **Edit**
3. Ve a la pestaña **Variables**
4. Modifica los valores según tu entorno (ej: cambiar `base_url` a `https://api-dev.ecicare.com/api`)
5. Click en **Save**

## 📁 Estructura de la Colección

### 1. **Modules** (Gestión de Módulos)
- ✅ Create Module
- ✅ Get All Modules
- ✅ Get All Modules (Paginated)
- ✅ Get Total Modules
- ✅ Get Challenges by Module
- ✅ Update Module
- ⚠️ **Delete Module (Cascade Delete)** - Elimina el módulo y TODOS sus challenges

### 2. **Challenges** (Gestión de Challenges)
- ✅ Create Challenge - Retorna `ChallengeResponse`
- ✅ Get All Challenges - Retorna `List<ChallengeResponse>`
- ✅ Get All Challenges (Paginated) - Retorna `Page<ChallengeResponse>`
- ✅ Search Challenges - Retorna `List<ModuleWithChallengesDTO>`
- ✅ Get Challenge by Name - Retorna `ChallengeResponse`
- ✅ Get Awards by Challenge - Retorna `List<AwardDto>`
- ✅ Get Challenges by User Email - Retorna `List<ChallengeResponse>`
- ✅ Get Confirmed Challenges by User - Retorna `List<ChallengeResponse>`
- ✅ Update Challenge - Retorna `ChallengeResponse`
- ✅ **Add User to Challenge** - Retorna `ChallengeResponse` (actualizado)
- ✅ **Confirm User in Challenge** - Retorna `ChallengeResponse` (actualizado)
- ✅ Delete Challenge

### 3. **Test Scenarios** (Escenarios de Prueba)
- 🧪 **Cascade Delete Test Flow** - Flujo completo de prueba de borrado en cascada

## 🧪 Prueba de Borrado en Cascada

### Opción 1: Ejecutar el Flujo Completo Automáticamente

1. Navega a **Test Scenarios > Cascade Delete Test Flow**
2. Click derecho en la carpeta
3. Selecciona **Run folder**
4. Click en **Run EciCare - Challenges & Modules API**
5. Observa cómo se ejecutan los 9 pasos automáticamente

**El flujo hace lo siguiente:**
1. ✅ Crea un módulo de prueba
2. ✅ Crea 3 challenges asociados al módulo
3. ✅ Verifica que los challenges existen
4. ⚠️ **ELIMINA EL MÓDULO** (borrado en cascada)
5. ✅ Verifica que los 3 challenges fueron eliminados automáticamente

### Opción 2: Prueba Manual Paso a Paso

1. **Crear un módulo:**
   - Ejecuta `Modules > Create Module`
   
2. **Crear varios challenges asociados:**
   - Ejecuta `Challenges > Create Challenge` varias veces
   - Asegúrate de que `moduleName` en el body sea el mismo que el módulo creado

3. **Verificar que los challenges existen:**
   - Ejecuta `Modules > Get Challenges by Module`
   - Deberías ver todos los challenges creados

4. **Eliminar el módulo (borrado en cascada):**
   - Ejecuta `Modules > Delete Module (Cascade Delete)`
   - ⚠️ Esto eliminará el módulo Y TODOS sus challenges

5. **Verificar que los challenges fueron eliminados:**
   - Ejecuta `Challenges > Get Challenge by Name` para cada challenge
   - Deberías recibir `null` o un error 404

## 🎯 Casos de Uso Principales

### Crear un Módulo con Challenges

```
1. POST /modules/ - Crear módulo
2. POST /challenges/ - Crear challenge 1 (con moduleName)
3. POST /challenges/ - Crear challenge 2 (con moduleName)
4. GET /modules/challenges/{moduleName} - Ver todos los challenges del módulo
```

### Probar el Borrado en Cascada

```
1. POST /modules/ - Crear módulo de prueba
2. POST /challenges/ - Crear múltiples challenges
3. GET /modules/challenges/{moduleName} - Verificar challenges
4. DELETE /modules/{moduleName} - ⚠️ BORRADO EN CASCADA
5. GET /challenges/{challengeName} - Verificar que fueron eliminados
```

### Gestionar Usuarios en Challenges

```
1. PUT /challenges/users/{email}/challenges/{name} - Registrar usuario (retorna ChallengeResponse)
2. GET /challenges/users/{email} - Ver challenges del usuario
3. PUT /challenges/users/{email}/challenges/{name}/confirm - Confirmar challenge (retorna ChallengeResponse)
4. GET /challenges/confirmed/{email} - Ver challenges confirmados
```

**Nota**: Los endpoints de Add User y Confirm User ahora retornan el challenge actualizado como `ChallengeResponse`, permitiendo al frontend actualizar inmediatamente sin necesidad de hacer un GET adicional.

## 📝 Notas Importantes

- ⚠️ **Borrado en Cascada**: Al eliminar un módulo, se eliminan TODOS los challenges asociados automáticamente
- 🔄 Todos los endpoints tienen tests automáticos que verifican el código de respuesta
- 📊 Los endpoints de paginación requieren los parámetros `page` y `size`
- 🔍 El endpoint de búsqueda agrupa los resultados por módulos
- 📧 Los endpoints de usuarios requieren un email válido registrado en el sistema

## 🌍 Entornos Sugeridos

Puedes crear diferentes entornos en Postman para distintos ambientes:

### Desarrollo Local
```
base_url: http://localhost:8080/api
```

### Desarrollo (Docker)
```
base_url: http://localhost:8080/api
```

### Staging
```
base_url: https://api-staging.ecicare.com/api
```

### Producción
```
base_url: https://api.ecicare.com/api
```

## 🐛 Troubleshooting

### Error de Conexión
- Verifica que el backend esté corriendo
- Confirma que la variable `base_url` sea correcta
- Revisa que el puerto sea el correcto (por defecto 8080)

### Challenge no se Elimina con el Módulo
- Verifica que la relación en la base de datos tenga `CASCADE` configurado
- Revisa los logs del backend para ver si hay errores

### Tests Fallan
- Asegúrate de que las variables de colección estén configuradas
- Verifica que los datos de prueba no existan previamente en la BD
- Ejecuta los requests en orden (especialmente en el flujo de cascade delete)

## 📞 Soporte

Para más información sobre el API, consulta:
- Documentación del código fuente
- Controladores: `ChallengeController.java` y `ModuleController.java`
- Servicios: `ChallengeService.java` y `ModuleService.java`

---

**Autor**: Byte Programming  
**Proyecto**: EciCare Backend  
**Última actualización**: 2025-10-06
