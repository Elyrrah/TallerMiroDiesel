<%-- 
    Document   : cliente_form
    Created on : 2 feb. 2026, 3:37:10 p. m.
    Author     : elyrr
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Nuevo Cliente</title>
</head>
<body>

    <p>
        <a href="${pageContext.request.contextPath}/">Volver al inicio</a>
    </p>

    <p>
        <a href="${pageContext.request.contextPath}/clientes?action=listar">Volver al listado</a>
    </p>

    <h2>Nuevo Cliente</h2>

    <c:if test="${not empty error}">
        <div style="color:#b00000; margin-bottom:10px;">
            ${error}
        </div>
    </c:if>

    <c:if test="${not empty errorDetalle}">
        <div style="color:#b00000; margin-bottom:10px;">
            ${errorDetalle}
        </div>
    </c:if>

    <%-- ===============================================================
         DATOS COMPLETOS DE DISTRITOS Y LOCALIDADES EMBEBIDOS EN JS
         Para que el datalist pueda filtrar localidades por distrito
         sin hacer llamadas al servidor.
         =============================================================== --%>
    <script>
        // Mapa: id_distrito → nombre del distrito
        const distritos = {
            <c:forEach var="d" items="${distritos}" varStatus="st">
            "${d.idDistrito}": "${d.nombre}"<c:if test="${!st.last}">,</c:if>
            </c:forEach>
        };

        // Lista completa de localidades con su distrito asociado
        const localidades = [
            <c:forEach var="l" items="${localidades}" varStatus="st">
            { id: "${l.idLocalidad}", nombre: "${l.nombre}", idDistrito: "${l.idDistrito}" }<c:if test="${!st.last}">,</c:if>
            </c:forEach>
        ];

        // Tipos de documento
        const tiposDocumento = {
            <c:forEach var="td" items="${tiposDocumento}" varStatus="st">
            "${td.idTipoDocumento}": "${td.nombre}"<c:if test="${!st.last}">,</c:if>
            </c:forEach>
        };
    </script>

    <form method="post" action="${pageContext.request.contextPath}/clientes">
        <input type="hidden" name="action" value="guardar"/>

        <!-- ================= TIPO DE CLIENTE ================= -->
        <fieldset style="margin-bottom:12px;">
            <legend>Tipo de Cliente</legend>

            <label>
                <input type="radio" name="tipo" value="PERSONA"
                       <c:if test="${tipo == 'PERSONA' or empty tipo}">checked</c:if>
                       onclick="toggleTipo('PERSONA')"/>
                Persona
            </label>

            <label style="margin-left:14px;">
                <input type="radio" name="tipo" value="EMPRESA"
                       <c:if test="${tipo == 'EMPRESA'}">checked</c:if>
                       onclick="toggleTipo('EMPRESA')"/>
                Empresa
            </label>
        </fieldset>

        <!-- ================= DATOS COMUNES ================= -->
        <fieldset style="margin-bottom:12px;">
            <legend>Datos Comunes</legend>

            Teléfono:<br/>
            <input type="text" name="telefono" value="${cliente.telefono}"/><br/><br/>

            <!-- ========== DISTRITO ========== -->
            <%-- El input visible es solo para el datalist.
                 El hidden es el que realmente se envía al servidor con el ID. --%>
            Distrito:<br/>
            <input type="text" id="inputDistrito" list="listDistritos"
                   placeholder="Escribí para buscar..." style="width:300px;"
                   autocomplete="off"/>
            <datalist id="listDistritos">
                <c:forEach var="d" items="${distritos}">
                    <option value="${d.nombre}" data-id="${d.idDistrito}"></option>
                </c:forEach>
            </datalist>
            <input type="hidden" name="id_distrito" id="hiddenDistrito"/>
            <br/><br/>

            <!-- ========== LOCALIDAD ========== -->
            Localidad:<br/>
            <input type="text" id="inputLocalidad" list="listLocalidades"
                   placeholder="Escribí para buscar..." style="width:300px;"
                   autocomplete="off"/>
            <datalist id="listLocalidades">
                <%-- Se llena con todas las localidades por defecto.
                     Si el usuario elige un distrito, el JS filtra solo las de ese distrito. --%>
                <c:forEach var="l" items="${localidades}">
                    <option value="${l.nombre}" data-id="${l.idLocalidad}" data-distrito="${l.idDistrito}"></option>
                </c:forEach>
            </datalist>
            <input type="hidden" name="id_localidad" id="hiddenLocalidad"/>
            <br/><br/>

        </fieldset>

        <!-- ================= PERSONA ================= -->
        <fieldset id="fsPersona" style="margin-bottom:12px;">
            <legend>Datos de Persona</legend>

            Nombre:<br/>
            <input type="text" name="nombre" value="${nombre}"/><br/><br/>

            Apellido:<br/>
            <input type="text" name="apellido" value="${apellido}"/><br/><br/>

            Apodo:<br/>
            <input type="text" name="apodo" value="${apodo}"/><br/>
        </fieldset>

        <!-- ================= EMPRESA ================= -->
        <fieldset id="fsEmpresa" style="margin-bottom:12px;">
            <legend>Datos de Empresa</legend>

            Razón Social:<br/>
            <input type="text" name="razon_social" value="${razon_social}" style="width:360px;"/><br/><br/>

            Nombre Fantasía:<br/>
            <input type="text" name="nombre_fantasia" value="${nombre_fantasia}"/><br/>
        </fieldset>

        <!-- ================= DOCUMENTO (ALTA INICIAL) ================= -->
        <fieldset style="margin-bottom:12px;">
            <legend>Documento (opcional)</legend>

            Tipo Documento:<br/>
            <input type="text" id="inputTipoDocumento" list="listTiposDocumento"
                   placeholder="Escribí para buscar..." style="width:300px;"
                   autocomplete="off"/>
            <datalist id="listTiposDocumento">
                <c:forEach var="td" items="${tiposDocumento}">
                    <option value="${td.nombre}" data-id="${td.idTipoDocumento}"></option>
                </c:forEach>
            </datalist>
            <input type="hidden" name="id_tipo_documento" id="hiddenTipoDocumento"/>
            <br/><br/>

            Número:<br/>
            <input type="text" name="numero_documento" value="${numero_documento}" style="width:260px;"/><br/><br/>

            <label>
                <input type="checkbox" name="principal_documento"
                       <c:if test="${principal_documento == 'on' or principal_documento == 'true' or principal_documento == '1'}">checked</c:if> />
                Marcar como principal
            </label>
        </fieldset>

        <button type="submit">Guardar</button>
    </form>

    <script>
        // ===============================================================
        // TOGGLE PERSONA / EMPRESA
        // ===============================================================
        function toggleTipo(tipo) {
            document.getElementById('fsPersona').style.display = (tipo === 'PERSONA') ? 'block' : 'none';
            document.getElementById('fsEmpresa').style.display = (tipo === 'EMPRESA') ? 'block' : 'none';
        }

        // ===============================================================
        // LÓGICA DE DATALIST CON HIDDEN INPUT
        // Conecta un input visible con su hidden que guarda el ID real.
        // ===============================================================

        // Cuando el usuario termina de escribir en el input de distrito:
        // 1. Busca si lo que escribió coincide exactamente con alguna opción
        // 2. Si coincide, guarda el ID en el hidden y filtra las localidades
        // 3. Si no coincide, limpia el hidden y restaura todas las localidades
        document.getElementById('inputDistrito').addEventListener('change', function () {
            const texto = this.value.trim().toUpperCase();
            const match = Object.entries(distritos).find(([id, nombre]) => nombre === texto);

            if (match) {
                const idDistrito = match[0];
                document.getElementById('hiddenDistrito').value = idDistrito;

                // Filtra el datalist de localidades para mostrar solo las de este distrito
                const datalist = document.getElementById('listLocalidades');
                datalist.innerHTML = '';
                localidades
                    .filter(l => l.idDistrito === idDistrito)
                    .forEach(l => {
                        const opt = document.createElement('option');
                        opt.value = l.nombre;
                        opt.setAttribute('data-id', l.id);
                        opt.setAttribute('data-distrito', l.idDistrito);
                        datalist.appendChild(opt);
                    });

                // Limpia la localidad elegida porque puede no pertenecer a este distrito
                document.getElementById('inputLocalidad').value = '';
                document.getElementById('hiddenLocalidad').value = '';

            } else {
                // El texto no coincide con ningún distrito conocido, limpia el hidden
                document.getElementById('hiddenDistrito').value = '';

                // Restaura todas las localidades en el datalist
                const datalist = document.getElementById('listLocalidades');
                datalist.innerHTML = '';
                localidades.forEach(l => {
                    const opt = document.createElement('option');
                    opt.value = l.nombre;
                    opt.setAttribute('data-id', l.id);
                    opt.setAttribute('data-distrito', l.idDistrito);
                    datalist.appendChild(opt);
                });
            }
        });

        // Cuando el usuario elige una localidad:
        // 1. Busca si lo que escribió coincide exactamente con alguna opción
        // 2. Si coincide, guarda el ID en el hidden
        // 3. Si la localidad tiene un distrito asociado y el campo distrito está vacío,
        //    lo autocompleta automáticamente
        document.getElementById('inputLocalidad').addEventListener('change', function () {
            const texto = this.value.trim().toUpperCase();

            // Busca en la lista actual del datalist (puede estar filtrada por distrito)
            const datalist = document.getElementById('listLocalidades');
            const opciones = Array.from(datalist.options);
            const match = opciones.find(o => o.value.toUpperCase() === texto);

            if (match) {
                document.getElementById('hiddenLocalidad').value = match.getAttribute('data-id');

                // Si el campo distrito estaba vacío, lo autocompleta
                const idDistrito = match.getAttribute('data-distrito');
                const hiddenDistrito = document.getElementById('hiddenDistrito');
                if (!hiddenDistrito.value && idDistrito && distritos[idDistrito]) {
                    document.getElementById('inputDistrito').value = distritos[idDistrito];
                    hiddenDistrito.value = idDistrito;
                }
            } else {
                document.getElementById('hiddenLocalidad').value = '';
            }
        });

        // Cuando el usuario elige un tipo de documento:
        // Busca el ID correspondiente y lo guarda en el hidden
        document.getElementById('inputTipoDocumento').addEventListener('change', function () {
            const texto = this.value.trim().toUpperCase();
            const match = Object.entries(tiposDocumento).find(([id, nombre]) => nombre.toUpperCase() === texto);

            if (match) {
                document.getElementById('hiddenTipoDocumento').value = match[0];
            } else {
                document.getElementById('hiddenTipoDocumento').value = '';
            }
        });

        // ===============================================================
        // INICIALIZACIÓN
        // ===============================================================
        (function init() {
            var tipo = '${tipo}';
            if (!tipo) tipo = 'PERSONA';
            toggleTipo(tipo);
        })();
    </script>

</body>
</html>