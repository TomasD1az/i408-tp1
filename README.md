# Plataforma Digital de Gift Cards - Backend

## Descripción

Este repositorio contiene el backend de una plataforma digital para gestionar Gift Cards. La empresa de retail y servicios desea ofrecer a sus clientes la posibilidad de consultar y gestionar el saldo y los detalles de sus tarjetas de regalo a través de un sistema web.

### Funcionalidades principales:

1. **Autenticación de usuarios:**
   - Los usuarios pueden iniciar sesión utilizando su nombre de usuario y contraseña.
   - Al iniciar sesión, se genera un **token de autenticación** que permite realizar las operaciones principales durante un tiempo limitado (5 minutos).

2. **Gestión de Gift Cards:**
   - Los usuarios pueden reclamar múltiples tarjetas de regalo a través del token generado.
   - El saldo y el detalle de los gastos de las tarjetas pueden ser consultados mediante el token de autenticación.
   
3. **Operaciones de Merchants:**
   - Los merchants registrados pueden generar cargos en las tarjetas de regalo reclamadas.
   - Los cargos afectan el balance de las tarjetas y se reflejan en los detalles de los gastos.

4. **Manejo de Tokens:**
   - Los tokens de autenticación tienen una validez de 5 minutos.
   - Una vez caducado, el usuario deberá volver a iniciar sesión para obtener un nuevo token.

### Requerimientos

- **Técnica de Desarrollo:** Desarrollo basado en **TDD** (Test-Driven Development).
- **Cobertura de tests:** Se requiere una completa cobertura de pruebas unitarias y de integración para asegurar el correcto funcionamiento de todas las funcionalidades.
- **Estándares de Código:**
  - Evitar la repetición de código.
  - Minimizar el uso de estructuras condicionales (como `if`).
  - Buenas prácticas en los nombres de variables y funciones.
  - Correcta distribución de responsabilidades entre objetos.
