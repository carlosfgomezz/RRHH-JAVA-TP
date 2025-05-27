# SISRH – Sistema Integral de Salarios y Recursos Humanos

## Descripción
Sistema de software en Java que permite la gestión de empleados y el cálculo automático de salarios conforme a la normativa vigente del IPS, incluyendo aportes patronales y personales, bonificaciones y deducciones legales.

## Módulos del Sistema
1. **Gestión de Empleados**
   - Alta, baja y modificación de datos de empleados
   - Historial laboral interno
   - Categorías y cargos

2. **Gestión de Salarios**
   - Cálculo del salario bruto
   - Cálculo de aportes IPS (patronal 16,5%, personal 9%)
   - Deducciones (adelantos, ausencias)
   - Generación de recibos de salario

3. **Gestión de Contratos**
   - Tipos de contratos (indefinido, a plazo fijo, pasantía)
   - Fechas de inicio y fin
   - Renovaciones automáticas o manuales

4. **Reportes**
   - Recibos mensuales por empleado
   - Reporte de aportes IPS por mes
   - Listado de nómina general
   - Exportación a PDF/Excel

5. **Seguridad y Usuarios**
   - Acceso por roles (admin, RRHH, contabilidad)
   - Historial de sesiones

6. **Configuración General**
   - Parámetros como salario mínimo, porcentaje IPS, bonificaciones
   - Fechas de pago

## Tecnologías Utilizadas
- Lenguaje: Java
- Interfaz Gráfica: JavaFX
- Base de Datos: ObjectDB

## Fuentes Normativas Implementadas
- Ley N° 98/92 (Sistema de jubilaciones y pensiones del IPS)
- Salario mínimo legal vigente
- Porcentajes de aporte personal (9%) y patronal (16.5%)