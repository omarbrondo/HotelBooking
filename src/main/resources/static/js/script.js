function authedFetch(url, opts = {}) {
  const token = localStorage.getItem("token");
  const headers = {
    ...opts.headers,
    "Content-Type": opts.headers?.["Content-Type"] || "application/json"
  };
  if (token) headers["Authorization"] = `Bearer ${token}`;
  return fetch(url, { ...opts, headers });
}

document.addEventListener("DOMContentLoaded", async () => {
  const reservationForm = document.getElementById("reservationForm");
  const occupiedSection = document.querySelector(".occupied-section");
  const facturasContainer = document.getElementById("facturasContainer");
  const optProductos = document.getElementById("optProductos");
  const productosContainer = document.getElementById("productosContainer");
  const formProducto = document.getElementById("productoForm");
  const tablaProductsBody = document.querySelector("#tablaProductos tbody");
  const inpProdId = document.getElementById("prodId");
  const inpProdNombre = document.getElementById("prodNombre");
  const inpProdPrecio = document.getElementById("prodPrecio");
  const optUsuarios = document.getElementById("optUsuarios");
  const usuariosContainer = document.getElementById("usuariosContainer");
  const usuarioForm = document.getElementById("usuarioForm");
  const tablaUsuariosBody = document.querySelector("#tablaUsuarios tbody");
  const inpUsuarioId = document.getElementById("usuarioId");
  const inpUsuarioNombre = document.getElementById("usuarioNombre");
  const inpUsuarioPass = document.getElementById("usuarioPass");
  const inpUsuarioRol = document.getElementById("usuarioRol");
  const token = localStorage.getItem("token");


   if (!token) {
    await pedirLogin();
  }     
  ajustarPorRol();
  inicializarApp(); 




document.getElementById("btnLogout")
  .addEventListener("click", () => {
    localStorage.clear();
    location.reload();
  });



  document.querySelectorAll(".btn-imprimir").forEach((btn) => {
    btn.addEventListener("click", () => {
      const id = btn.dataset.id;
      const factura = facturas.find((f) => f.id == id);
      window.descargarPdf(factura);
    });
  });



// 2. Función utilitaria para ocultar todas las secciones
function hideAllSections() {
  reservationForm.hidden    = true;
  occupiedSection.hidden    = true;
  productosContainer.hidden = true;
  usuariosContainer.hidden  = true;
  facturasContainer.hidden  = true;
}








  /*---------------------------------------------------------- */
  // 1) función para cargar facturas
  /*---------------------------------------------------------- */

  async function cargarFacturas() {
    try {
      const resp = await authedFetch("/api/facturas");
      const facturas = await resp.json();
      const cont = document.getElementById("facturasContainer");

      // 1) Armo la tabla (agregué columna “Acción”)
      let html = `
      <table class="table table-striped">
        <thead>
          <tr>
            <th>#</th><th>Fecha</th><th>Reserva ID</th>
            <th>Total Habitación</th><th>Total Consumos</th><th>Total Final</th>
            <th>Acción</th>
          </tr>
        </thead>
        <tbody>
    `;
      facturas.forEach((f) => {
        html += `
        <tr>
          <td>${f.id}</td>
          <td>${new Date(f.fechaFactura).toLocaleString()}</td>
          <td>${f.reservaId}</td>
          <td>$${f.totalHabitacion.toFixed(2)}</td>
          <td>$${f.totalConsumos.toFixed(2)}</td>
          <td><strong>$${f.totalFinal.toFixed(2)}</strong></td>
          <td>
            <button 
              class="btn btn-outline-primary btn-sm btn-imprimir" 
              data-id="${f.id}">
              <i class="fa fa-print"></i>
            </button>
          </td>
        </tr>
      `;
      });
      html += `</tbody></table>`;
      cont.innerHTML = html;

      // 2) Recorro los botones y les pongo el handler
      cont.querySelectorAll(".btn-imprimir").forEach((btn) => {
        btn.addEventListener("click", () => {
          const id = btn.dataset.id;
          // busco en el array la factura correspondiente
          const factura = facturas.find((x) => x.id == id);
          if (!factura) {
            return Swal.fire("Error", "No se encontró la factura", "error");
          }
          // llamo a tu función que genera y descarga el PDF
          descargarPdf(factura);
        });
      });
    } catch (e) {
      console.error("Error al cargar facturas:", e);
      Swal.fire("Error", "No se pudieron cargar las facturas", "error");
    }
  }

  /*---------------------------------------------------------- */
  // FIN de función para cargar facturas
  /*---------------------------------------------------------- */

  /*---------------------------------------------------------- */
  // 2) función para cargar productos
  /*---------------------------------------------------------- */
async function cargarProductos() {
   try {
    const resp = await authedFetch("/api/productos/all");
    if (!resp.ok) throw new Error(await resp.text());
    const prods = await resp.json();

    // 2) Vacio el tbody
    const tbody = document.querySelector("#tablaProductos tbody");
    tbody.innerHTML = "";

    // 3) Por cada producto, creo una fila con switch y botón editar
    prods.forEach(p => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${p.idProducto}</td>
        <td>${p.nombreProducto}</td>
        <td>$${p.precio.toFixed(2)}</td>
        <td>
          <div class="form-check form-switch">
            <input
              class="form-check-input toggle-activo"
              type="checkbox"
              id="switch-${p.idProducto}"
              data-id="${p.idProducto}"
              ${p.activo ? "checked" : ""}
            />
            <label
              class="form-check-label"
              for="switch-${p.idProducto}"
            >${p.activo ? "Activo" : "Inactivo"}</label>
          </div>
        </td>
        <td>
          <button class="btn btn-sm btn-warning btn-edit" data-id="${p.idProducto}">
            Editar
          </button>
        </td>`;
      tbody.appendChild(tr);
    });


    // 4) Listeners editar
    document.querySelectorAll(".btn-edit")
      .forEach(b => b.addEventListener("click", onClickEditar));

    // 5) Listeners switch
    document.querySelectorAll(".toggle-activo")
      .forEach(cb => cb.addEventListener("change", async e => {
        const id = e.target.dataset.id;
        try {
          if (e.target.checked) {
            // Reactivar
            const r = await authedFetch(`/api/productos/${id}/restaurar`, { method: "PATCH" });
            if (!r.ok) throw new Error(await r.text());
            Swal.fire("Activado", "Producto reactivado", "success");
          } else {
            // Dar de baja
            const r = await authedFetch(`/api/productos/${id}`, { method: "DELETE" });
            if (!r.ok) throw new Error(await r.text());
            Swal.fire("Desactivado", "Producto dado de baja", "success");
          }
          // Opcional: refrescar la grilla
          await cargarProductos();
        } catch (err) {
          Swal.fire("Error", err.message, "error");
          // Revertir posición del switch en caso de error
          e.target.checked = !e.target.checked;
        }
      }));

  } catch (err) {
    console.error("Error al cargar productos:", err);
    Swal.fire("Error", err.message, "error");
  }
}



  // 3. Listener para “Reservas”
optReservas.addEventListener("click", e => {
  e.preventDefault();
  setActive("optReservas");

  hideAllSections();
  // mostramos solo las secciones de reservas
  reservationForm.hidden   = false;
  occupiedSection.hidden   = false;
});

  /*---------------------------------------------------------- */
  // FIN de función para cargar productos
  /*---------------------------------------------------------- */

  // 1) Mostrar sección Productos en una reserva
// 4. (Opcional) refactor de los otros listeners:
optProductos.addEventListener("click", e => {
  e.preventDefault();
  setActive("optProductos");
  hideAllSections();
  productosContainer.hidden = false;
  cargarProductos();
});

  optUsuarios.addEventListener("click", e => {
  e.preventDefault();
  setActive("optUsuarios");
  hideAllSections();
  usuariosContainer.hidden = false;
  cargarUsuarios();
});

  // 3) Cargar listado de usuarios (sin password)
  async function cargarUsuarios() {
    try {
      const res = await authedFetch("/usuario/dto");
      const list = await res.json(); // List<UsuarioDTO>
      tablaUsuariosBody.innerHTML = "";
      list.forEach((u) => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
        <td>${u.idUsuario}</td>
        <td>${u.nombreUsuario}</td>
        <td>${u.rol}</td>
        <td>
          <button class="btn btn-sm btn-warning btn-edit-usr" data-id="${u.idUsuario}">Editar</button>
          <button class="btn btn-sm btn-danger btn-del-usr"  data-id="${u.idUsuario}">Borrar</button>
        </td>`;
        tablaUsuariosBody.appendChild(tr);
      });

      // enganchar eventos
      tablaUsuariosBody
        .querySelectorAll(".btn-edit-usr")
        .forEach((b) => b.addEventListener("click", onClickEditarUsuario));
      tablaUsuariosBody
        .querySelectorAll(".btn-del-usr")
        .forEach((b) => b.addEventListener("click", onClickBorrarUsuario));
    } catch (err) {
      console.error(err);
      Swal.fire("Error", "No se pudieron cargar usuarios", "error");
    }
  }

  // 4) Editar usuario: trae password
  async function onClickEditarUsuario(e) {
    const id = e.target.dataset.id;
    const res = await authedFetch(`/usuario/${id}`);
    if (!res.ok) return Swal.fire("Error", "Usuario no existe", "error");
    const u = await res.json(); // Usuario completo
    inpUsuarioId.value = u.idUsuario;
    inpUsuarioNombre.value = u.nombreUsuario;
    inpUsuarioPass.value = u.password;
    inpUsuarioRol.value = u.rol;
  }

  // 5) Borrar usuario
  function onClickBorrarUsuario(e) {
    const id = e.target.dataset.id;
    Swal.fire({
      title: "¿Confirmar borrado?",
      icon: "warning",
      showCancelButton: true,
    }).then(async (ans) => {
      if (!ans.isConfirmed) return;
      await authedFetch(`/usuario/${id}`, { method: "DELETE" });
      await cargarUsuarios();
      Swal.fire("OK", "Usuario eliminado", "success");
    });
  }

  // 6) Alta / Edición en el form
  usuarioForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const id = inpUsuarioId.value;
    const payload = {
      nombreUsuario: inpUsuarioNombre.value.trim(),
      password: inpUsuarioPass.value,
      rol: inpUsuarioRol.value,
    };
    const url = id ? `/usuario/${id}` : `/usuario`;
    const method = id ? "PUT" : "POST";

    try {
      const res = await authedFetch(url, {
        method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });
      if (!res.ok) throw new Error(await res.text());
      await cargarUsuarios();
      usuarioForm.reset();
      inpUsuarioId.value = "";
      Swal.fire("OK", "Usuario guardado", "success");
    } catch (err) {
      Swal.fire("Error", err.message, "error");
    }
  });

  // 3) Alta / edición de producto
  formProducto.addEventListener("submit", async (e) => {
    e.preventDefault();
    const id = inpProdId.value;
    const body = {
      nombreProducto: inpProdNombre.value.trim(),
      precio: +inpProdPrecio.value,
    };
    const url = id ? `/api/productos/${id}` : `/api/productos`;
    const method = id ? "PUT" : "POST";

    try {
      const res = await authedFetch(url, {
        method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body),
      });
      if (!res.ok) throw new Error(await res.text());
      await cargarProductos();
      formProducto.reset();
      inpProdId.value = "";
      Swal.fire("OK", "Producto guardado", "success");
    } catch (e) {
      Swal.fire("Error", e.message, "error");
    }
  });

  // 4) Click en “Editar”
  async function onClickEditar(e) {
    const id = e.target.dataset.id;
    const res = await authedFetch(`/api/productos/${id}`);
    const p = await res.json();
    inpProdId.value = p.idProducto;
    inpProdNombre.value = p.nombreProducto;
    inpProdPrecio.value = p.precio;
  }

  // 5) Click en “Borrar”
  function onClickBorrar(e) {
    const id = e.target.dataset.id;
    Swal.fire({
      title: "¿Confirmar borrado?",
      icon: "warning",
      showCancelButton: true,
    }).then(async (ans) => {
      if (!ans.isConfirmed) return;
      await authedFetch(`/api/productos/${id}`, { method: "DELETE" });
      await cargarProductos();
      Swal.fire("OK", "Producto eliminado", "success");
    });
  }

  document.getElementById("optUsuarios").addEventListener("click", (e) => {
    e.preventDefault();
    setActive("optUsuarios");
    console.log("Mostrar Usuarios");
  });

  // 2) Listener para el botón "Facturas" en el menú
  // listener para pestaña Facturas
  document.getElementById("optFacturas").addEventListener("click", (e) => {
    e.preventDefault();
    setActive("optFacturas");

    // **oculto el form de reservas y ocupadas**
    reservationForm.hidden = true;
    occupiedSection.hidden = true;
    productosContainer.hidden = true;

    // **muestro solo facturas**
    facturasContainer.hidden = false;
    cargarFacturas();
  });

  // 3) Asegúrate de que, al inicio, la sección facturas esté oculta
  facturasContainer.hidden = true;
});




function setActive(id) {
  document
    .querySelectorAll(".nav-link")
    .forEach((a) => a.classList.remove("active"));
  document.getElementById(id).classList.add("active");
}

/**
 * Carga una imagen desde una URL y la convierte a DataURL (Base64).
 * @param {string} url – Ruta pública de tu logo (p.ej. '/img/logo.png').
 * @returns {Promise<string>} – Una promise que resuelve con el dataURL.
 */
function loadImageAsDataURL(url) {
  return authedFetch(url)
    .then((res) => res.blob())
    .then(
      (blob) =>
        new Promise((resolve, reject) => {
          const reader = new FileReader();
          reader.onloadend = () => resolve(reader.result);
          reader.onerror = reject;
          reader.readAsDataURL(blob);
        })
    );
}

window.descargarPdf = async function (factura) {
  // 1) Generamos el DataURL del logo en tiempo real
  const logoDataUrl = await loadImageAsDataURL(
    "/img/hotel-logo-design-service.png"
  );

  // 2) Creamos el PDF
  const doc = new window.jspdf.jsPDF({ unit: "pt", format: "a4" });
  const W = doc.internal.pageSize.getWidth();
  const H = doc.internal.pageSize.getHeight();
  const margin = 40;
  const startY = 100;
  const lh = 18;

  // 3) Header con logo y datos
  doc.addImage(logoDataUrl, "PNG", margin, 20, 80, 40);
  doc.setFont("helvetica", "bold").setFontSize(18);
  doc.text("Hotel Acme", margin + 90, 40);
  doc.setFont("helvetica", "normal").setFontSize(12);
  doc.text(`Factura #${factura.id}`, W - margin, 30, { align: "right" });
  doc.text(
    `Fecha: ${new Date(factura.fechaFactura).toLocaleString()}`,
    W - margin,
    48,
    { align: "right" }
  );
  doc.text(`Reserva ID: ${factura.reservaId}`, W - margin, 66, {
    align: "right",
  });

  // 4) Tabla de detalle con autoTable
  const head = [["Producto", "Cantidad", "P.U.", "Subtotal"]];
  const body = factura.detalles.map((d) => [
    d.producto.nombreProducto,
    d.cantidad,
    `$${d.precioUnitario.toFixed(2)}`,
    `$${d.subtotal.toFixed(2)}`,
  ]);

  doc.autoTable({
    startY,
    margin: { left: margin, right: margin },
    head,
    body,
    styles: {
      font: "helvetica",
      fontSize: 10,
      lineColor: [220, 220, 220],
      lineWidth: 0.5,
    },
    headStyles: {
      fillColor: [52, 73, 94],
      textColor: 255,
      halign: "center",
    },
    alternateRowStyles: {
      fillColor: [245, 245, 245],
    },
    didDrawPage: (data) => {
      // Pie de página
      const pageNum = doc.internal.getNumberOfPages();
      doc
        .setFontSize(10)
        .setTextColor(150)
        .text(`Página ${pageNum}`, W / 2, H - margin / 2, { align: "center" });
    },
  });

  // 5) Totales abajo de la tabla
  const finalY = doc.lastAutoTable.finalY + lh;
  doc.setFont("helvetica", "bold").setFontSize(12);
  doc.text(
    `Total Habitación: $${factura.totalHabitacion.toFixed(2)}`,
    margin,
    finalY
  );
  doc.text(
    `Total Consumos:   $${factura.totalConsumos.toFixed(2)}`,
    margin,
    finalY + lh
  );
  doc.setFontSize(14);
  doc.text(
    `TOTAL: $${factura.totalFinal.toFixed(2)}`,
    W - margin,
    finalY + lh * 2,
    { align: "right" }
  );

  // 6) Descargar
  doc.save(`Factura_${factura.id}.pdf`);
  // Función pro que ya incluye tu logo dinámico
};






async function pedirLogin() {




  const BACKDROP = `
    rgba(255,255,255,0.9)
    url('/img/patron-tematico-viaje-varias-ilustraciones-sobre-fondo-vectorial-repeticion-tema_1030164-4.avif')
    repeat
  `;

  while (true) {
    const { value: cred } = await Swal.fire({
      title: "Iniciar Sesión",
      html:
        `<input id="swal-username" class="swal2-input" placeholder="Usuario">` +
        `<input id="swal-password" type="password" class="swal2-input" placeholder="Clave">`,
      focusConfirm: false,
      preConfirm: () => {
        const nombre = document.getElementById("swal-username").value.trim();
        const pass   = document.getElementById("swal-password").value.trim();
        if (!nombre || !pass)
          Swal.showValidationMessage("Completa usuario y contraseña");
        return { nombreUsuario: nombre, password: pass };
      },
      backdrop: BACKDROP,
    });

    if (!cred) {
      location.reload();
      return;
    }

    try {
      // 1) LOGIN SIN authedFetch
      const res = await fetch("/usuario/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(cred)
      });
      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || "Usuario/clave incorrectos");
      }

      // 2) UNA SOLA lectura del body
      const { token, idUsuario, rol } = await res.json();
      console.log("Login OK:", { token, idUsuario, rol });

      // 3) Guarda token y rol
      localStorage.setItem("token", token);
      window.currentUserRole = rol;
      console.log("ROL LOGUEADO:", rol);

      // 4) Ajusta UI y regresa
      ajustarPorRol();
      return;

    } catch (err) {
      await Swal.fire({
        icon: "error",
        title: "Error de autenticación",
        text: err.message,
        backdrop: BACKDROP,
      });
      // el bucle repite el login tras cerrar este modal
    }
  }
}


function ajustarPorRol() {
  const rol = window.currentUserRole;
  // Si es EMPLEADO: no muestro menú (solo reservas)
  if (rol === "EMPLEADO") {
    // oculto items de menú menos Reservas
    document.querySelectorAll(".nav-link")
      .forEach(a => {
        if (a.id !== "optReservas") a.style.display = "none";
      });

    // oculto delete en tabla de ocupadas
    document.querySelectorAll("#tablaOcupadas .btn-danger")
      .forEach(b => b.style.display = "none");
  }
  // Si es ADMINISTRADOR: dejo todo
}









function inicializarApp() {
  const reservationForm = document.getElementById("reservationForm");
  const habitacionSelect = document.getElementById("habitacion");
  const tablaOcupadasBody = document.querySelector("#tablaOcupadas tbody");

  // Función para cargar las habitaciones libres en el select
  function cargarHabitacionesLibres() {
    authedFetch("/api/habitaciones/libres")
      .then((response) => response.json())
      .then((data) => {
        habitacionSelect.innerHTML = `<option value="">Seleccione una habitación</option>`;
        data.forEach((habitacion) => {
          const option = document.createElement("option");
          option.value = habitacion.idHabitacion;
          option.text = `${habitacion.nombreHabitacion} (Precio: $${habitacion.precio})`;
          habitacionSelect.appendChild(option);
        });
      })
      .catch((error) => {
        console.error("Error al cargar habitaciones:", error);
        Swal.fire({
          icon: "error",
          title: "Error",
          text: "Error al cargar las habitaciones disponibles.",
        });
      });
  }

  // Función para cargar las habitaciones ocupadas y construir la tabla
  function cargarHabitacionesOcupadas() {
    authedFetch("/api/habitaciones/ocupadas")
      .then((response) => response.json())
      .then((data) => {
        tablaOcupadasBody.innerHTML = "";
        data.forEach((habitacion) => {
          const tr = document.createElement("tr");

          // Columna: Nombre de la habitación
          const tdHabitacion = document.createElement("td");
          tdHabitacion.textContent = habitacion.nombreHabitacion;
          tr.appendChild(tdHabitacion);

          // Columna: Precio
          const tdPrecio = document.createElement("td");
          tdPrecio.textContent = `$${habitacion.precio}`;
          tr.appendChild(tdPrecio);

          // Columna: Huésped
          const tdHuesped = document.createElement("td");
          if (habitacion.reserva) {
            tdHuesped.textContent = `${habitacion.reserva.nombre} ${habitacion.reserva.apellido}`;
          } else {
            tdHuesped.textContent = "Sin reserva";
          }
          tr.appendChild(tdHuesped);

          // Columna: Fecha Desde
          const tdFechaDesde = document.createElement("td");
          tdFechaDesde.textContent = habitacion.reserva
            ? habitacion.reserva.fechaDesde
            : "";
          tr.appendChild(tdFechaDesde);

          // Columna: Fecha Hasta
          const tdFechaHasta = document.createElement("td");
          tdFechaHasta.textContent = habitacion.reserva
            ? habitacion.reserva.fechaHasta
            : "";
          tr.appendChild(tdFechaHasta);

          // Columna: Acciones
          const tdAcciones = document.createElement("td");

          // Botón "Editar" (Bootstrap: btn, btn-primary, btn-sm)
          const btnEditar = document.createElement("button");
          btnEditar.textContent = "Editar";
          btnEditar.classList.add("btn", "btn-primary", "btn-sm");
          btnEditar.addEventListener("click", () => {
            if (!habitacion.reserva) {
              Swal.fire({
                icon: "info",
                title: "Sin reserva",
                text: "No hay datos para editar en esta habitación.",
              });
              return;
            }
            Swal.fire({
              title: "Editar Reserva",
              html:
                `<input id="swal-input1" class="swal2-input" placeholder="Nombre" value="${habitacion.reserva.nombre}">` +
                `<input id="swal-input2" class="swal2-input" placeholder="Apellido" value="${habitacion.reserva.apellido}">` +
                `<input id="swal-input3" class="swal2-input" placeholder="DNI" value="${habitacion.reserva.dni}">` +
                `<input id="swal-input4" type="date" class="swal2-input" value="${habitacion.reserva.fechaDesde}">` +
                `<input id="swal-input5" type="date" class="swal2-input" value="${habitacion.reserva.fechaHasta}">`,
              focusConfirm: false,
              preConfirm: () => {
                return {
                  nombre: document.getElementById("swal-input1").value,
                  apellido: document.getElementById("swal-input2").value,
                  dni: document.getElementById("swal-input3").value,
                  fechaDesde: document.getElementById("swal-input4").value,
                  fechaHasta: document.getElementById("swal-input5").value,
                };
              },
            }).then((result) => {
              if (result.isConfirmed) {
                const updatedData = result.value;
                authedFetch(`/api/reservas/${habitacion.reserva.idReserva}`, {
                  method: "PUT",
                  headers: { "Content-Type": "application/json" },
                  body: JSON.stringify(updatedData),
                })
                  .then((response) => {
                    if (!response.ok) {
                      return response.text().then((text) => {
                        throw new Error(text);
                      });
                    }
                    return response.json();
                  })
                  .then((data) => {
                    Swal.fire({
                      icon: "success",
                      title: "¡Actualizado!",
                      text: "La reserva se actualizó correctamente.",
                    });
                    cargarHabitacionesOcupadas();
                  })
                  .catch((error) => {
                    Swal.fire({
                      icon: "error",
                      title: "Error",
                      text: error.message,
                    });
                  });
              }
            });
          });
          tdAcciones.appendChild(btnEditar);

          // Botón "Agregar productos" (Bootstrap: btn, btn-success, btn-sm)
          const btnAgregarProductos = document.createElement("button");
          btnAgregarProductos.textContent = "Agregar productos";
          btnAgregarProductos.classList.add("btn", "btn-success", "btn-sm");
          btnAgregarProductos.style.marginLeft = "5px";
          btnAgregarProductos.addEventListener("click", () => {
            if (!habitacion.reserva) {
              Swal.fire({
                icon: "info",
                title: "Sin reserva",
                text: "No hay reserva asociada para agregar productos.",
              });
              return;
            }
            authedFetch("/api/productos")
              .then((resp) => resp.json())
              .then((productos) => {
                let htmlProductos = `<table class="table table-striped" style="text-align:center;">
                  <thead>
                    <tr>
                      <th>Producto</th>
                      <th>Precio</th>
                      <th>Cantidad</th>
                    </tr>
                  </thead>
                  <tbody>`;
                productos.forEach((prod) => {
                  htmlProductos += `<tr>
                      <td>${prod.nombreProducto}</td>
                      <td>$${prod.precio}</td>
                      <td><input type="number" min="0" value="0" id="cantidad-${prod.idProducto}" style="width:50px;" /></td>
                    </tr>`;
                });
                htmlProductos += `</tbody></table>`;

                Swal.fire({
                  title: "Agregar Productos",
                  html: htmlProductos,
                  showCancelButton: true,
                  confirmButtonText: "Guardar",
                  preConfirm: () => {
                    const consumos = [];
                    productos.forEach((prod) => {
                      const cantidad = document.getElementById(
                        `cantidad-${prod.idProducto}`
                      ).value;
                      const cant = parseInt(cantidad);
                      if (cant > 0) {
                        consumos.push({
                          idProducto: prod.idProducto,
                          cantidad: cant,
                        });
                      }
                    });
                    if (consumos.length === 0) {
                      Swal.showValidationMessage(
                        "Debes seleccionar al menos un producto con cantidad mayor a 0"
                      );
                    }
                    return consumos;
                  },
                }).then((result) => {
                  if (result.isConfirmed) {
                    const consumos = result.value;
                    const reservaId = habitacion.reserva.idReserva;
                    Promise.all(
                      consumos.map((consumo) =>
                        authedFetch(`/api/reservas/${reservaId}/consumos`, {
                          method: "POST",
                          headers: { "Content-Type": "application/json" },
                          body: JSON.stringify(consumo),
                        })
                      )
                    )
                      .then(() => {
                        Swal.fire({
                          icon: "success",
                          title: "Guardado",
                          text: "Productos agregados a la reserva",
                        });
                        cargarHabitacionesOcupadas();
                      })
                      .catch((error) => {
                        Swal.fire({
                          icon: "error",
                          title: "Error",
                          text: error.message,
                        });
                      });
                  }
                });
              })
              .catch((error) => {
                Swal.fire({
                  icon: "error",
                  title: "Error",
                  text: "No se pudo cargar los productos.",
                });
              });
          });
          tdAcciones.appendChild(btnAgregarProductos);

          // Botón "Detalle" (Bootstrap: btn, btn-warning, btn-sm)
          const btnDetalle = document.createElement("button");
          btnDetalle.textContent = "Detalle";
          btnDetalle.classList.add("btn", "btn-warning", "btn-sm");
          btnDetalle.style.marginLeft = "5px";
          btnDetalle.addEventListener("click", () => {
            if (!habitacion.reserva) {
              Swal.fire({
                icon: "info",
                title: "Sin reserva",
                text: "No hay datos de reserva para mostrar.",
              });
              return;
            }
            authedFetch(`/api/reservas/${habitacion.reserva.idReserva}`)
              .then((response) => response.json())
              .then((detalle) => {
                let htmlDetalle = `<strong>Habitación:</strong> ${habitacion.nombreHabitacion} <br/>
                                    <strong>Precio de habitación:</strong> $${habitacion.precio} <br/><hr/>
                                    <strong>Huésped:</strong> ${detalle.nombre} ${detalle.apellido} <br/>
                                    <strong>DNI:</strong> ${detalle.dni}<br/>
                                    <strong>Fechas:</strong> ${detalle.fechaDesde} - ${detalle.fechaHasta} <br/><hr/>`;
                if (detalle.consumos && detalle.consumos.length > 0) {
                  htmlDetalle += `<strong>Consumos:</strong><br/><table class="table table-striped">
                                    <thead>
                                      <tr>
                                        <th>Producto</th>
                                        <th>Cantidad</th>
                                        <th>Precio Unitario</th>
                                      </tr>
                                    </thead>
                                    <tbody>`;
                  detalle.consumos.forEach((consumo) => {
                    htmlDetalle += `<tr>
                                      <td>${consumo.producto.nombreProducto}</td>
                                      <td>${consumo.cantidad}</td>
                                      <td>$${consumo.producto.precio}</td>
                                    </tr>`;
                  });
                  htmlDetalle += `</tbody></table>`;
                } else {
                  htmlDetalle += `<strong>Consumos:</strong> Sin consumos registrados.`;
                }

                Swal.fire({
                  title: "Detalle de Reserva",
                  html: htmlDetalle,
                  width: "600px",
                });
              })
              .catch((error) => {
                Swal.fire({
                  icon: "error",
                  title: "Error",
                  text: "No se pudo cargar el detalle de la reserva.",
                });
              });
          });
          tdAcciones.appendChild(btnDetalle);

         // … dentro de tu loop al construir tdAcciones …
const btnEliminar = document.createElement("button");
btnEliminar.textContent = "Eliminar";
btnEliminar.classList.add("btn", "btn-danger", "btn-sm");
btnEliminar.style.marginLeft = "5px";
btnEliminar.addEventListener("click", () => {
  // 1) Compruebo rol
  if (window.currentUserRole !== "ADMINISTRADOR") {
    Toastify({
      text: "No tienes permisos para eliminar",
      duration: 3000,
      close: true,
      gravity: "top",
      position: "right",
      style: {
        background: "linear-gradient(to right, #D32F2F, #C62828)"
      }
    }).showToast();
    return;
  }

  // 2) Si es ADMINISTRADOR, prosigo con el SweetAlert de confirmación
  if (!habitacion.reserva) {
    Swal.fire({
      icon: "info",
      title: "Sin reserva",
      text: "No hay reserva para eliminar."
    });
    return;
  }

  Swal.fire({
    title: "¿Estás seguro?",
    text: "Se eliminará la reserva, sus consumos y la habitación quedará libre.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Sí, eliminar",
    cancelButtonText: "Cancelar"
  }).then(result => {
    if (!result.isConfirmed) return;
    authedFetch(`/api/reservas/${habitacion.reserva.idReserva}`, {
      method: "DELETE"
    })
    .then(response => {
      if (!response.ok) {
        return response.text().then(text => { throw new Error(text) });
      }
      return response;
    })
    .then(() => {
      Swal.fire("Eliminado", "La reserva se eliminó correctamente.", "success");
      cargarHabitacionesOcupadas();
      cargarHabitacionesLibres();
    })
    .catch(error => {
      Swal.fire("Error", error.message, "error");
    });
  });
});

tdAcciones.appendChild(btnEliminar);


          // Botón "Checkout" (Bootstrap: btn, btn-info, btn-sm)
          const btnCheckout = document.createElement("button");
          btnCheckout.textContent = "Checkout";
          btnCheckout.classList.add("btn", "btn-info", "btn-sm");
          btnCheckout.style.marginLeft = "5px";
          btnCheckout.addEventListener("click", () => {
            if (!habitacion.reserva) {
              Swal.fire({
                icon: "info",
                title: "Sin reserva",
                text: "No hay reserva para realizar el checkout.",
              });
              return;
            }

            // Se obtiene el detalle actualizado de la reserva
            authedFetch(`/api/reservas/${habitacion.reserva.idReserva}`)
              .then((response) => response.json())
              .then((reservaDetalle) => {
                // Calcular cantidad de días (suponiendo que las fechas sean ISO string "YYYY-MM-DD")
                const fechaDesde = new Date(reservaDetalle.fechaDesde);
                const fechaHasta = new Date(reservaDetalle.fechaHasta);
                const diffTime = Math.abs(fechaHasta - fechaDesde);
                const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

                // Calcular total por habitacion
                const totalHabitacion = habitacion.precio * diffDays;

                // Armar detalles de consumos
                let totalConsumos = 0;
                let detallesConsumosHTML = "";
                if (
                  reservaDetalle.consumos &&
                  reservaDetalle.consumos.length > 0
                ) {
                  reservaDetalle.consumos.forEach((consumo) => {
                    const subtotal = consumo.producto.precio * consumo.cantidad;
                    totalConsumos += subtotal;
                    detallesConsumosHTML += `<tr>
                      <td>${consumo.producto.nombreProducto}</td>
                      <td>${consumo.cantidad}</td>
                      <td>$${consumo.producto.precio.toFixed(2)}</td>
                      <td>$${subtotal.toFixed(2)}</td>
                    </tr>`;
                  });
                }

                const totalFinal = totalHabitacion + totalConsumos;

                // Armar HTML de la factura
                const htmlFactura = `
  <div class="container">
    <h3 class="text-center mb-3">Factura</h3>
    <div class="mb-3">
      <strong>Cliente:</strong> ${reservaDetalle.nombre} ${
                  reservaDetalle.apellido
                }<br/>
      <strong>DNI:</strong> ${reservaDetalle.dni}<br/>
      <strong>Habitación:</strong> ${
        habitacion.nombreHabitacion
      } ($${habitacion.precio.toFixed(2)} x día)<br/>
      <strong>Fechas:</strong> ${reservaDetalle.fechaDesde} - ${
                  reservaDetalle.fechaHasta
                } (${diffDays} días)<br/>
      <strong>Total Habitación:</strong> $${totalHabitacion.toFixed(2)}
    </div>
    <hr/>
    <h5>Detalle de consumos:</h5>
    <table class="table table-bordered">
      <thead>
        <tr>
          <th>Producto</th>
          <th>Cantidad</th>
          <th>Precio Unitario</th>
          <th>Subtotal</th>
        </tr>
      </thead>
      <tbody>
        ${
          detallesConsumosHTML ||
          '<tr><td colspan="4" class="text-center">Sin consumos</td></tr>'
        }
      </tbody>
    </table>
    <hr/>
    <h5 class="text-end">Total consumos: $${totalConsumos.toFixed(2)}</h5>
    <h4 class="text-end">TOTAL: $${totalFinal.toFixed(2)}</h4>
    <div class="text-center mt-4">
      <button id="btnConfirmarCheckout" class="btn btn-danger ms-2">Confirmar Checkout</button>
      <button id="btnCancelarCheckout" class="btn btn-secondary ms-2">Cancelar Checkout</button>
    </div>
  </div>
`;

                Swal.fire({
                  title: "Factura de Checkout",
                  html: htmlFactura,
                  width: "800px",
                  showConfirmButton: false,
                });
                document
                  .getElementById("btnCancelarCheckout")
                  .addEventListener("click", () => {
                    Swal.close();
                  });

                // Evento para confirmar checkout y eliminar la reserva
                // en lugar de DELETE /api/reservas...
                document
                  .getElementById("btnConfirmarCheckout")
                  .addEventListener("click", () => {
                    authedFetch(`/api/facturas/${habitacion.reserva.idReserva}`, {
                      method: "POST",
                    })
                      .then((r) => {
                        if (!r.ok)
                          return r.text().then((t) => {
                            throw new Error(t);
                          });
                        return r.json();
                      })
                      .then((factura) => {
                        descargarPdf(factura);

                        Swal.fire({
                          icon: "success",
                          title: "Checkout finalizado",
                          html: `
          <p>Factura #${factura.id}</p>
          <p>Total habitación: $${factura.totalHabitacion}</p>
          <p>Total consumos: $${factura.totalConsumos}</p>
          <h4>Total: $${factura.totalFinal}</h4>
        `,
                        });
                        // recarga UI
                        cargarHabitacionesOcupadas();
                        cargarHabitacionesLibres();
                      })
                      .catch((err) => {
                        Swal.fire("Error", err.message, "error");
                      });
                  });
              })
              .catch((error) => {
                Swal.fire({
                  icon: "error",
                  title: "Error",
                  text: "No se pudo generar la factura de checkout.",
                });
              });
          });
          tdAcciones.appendChild(btnCheckout);

          // Agrega la celda de acciones a la fila
          tr.appendChild(tdAcciones);
          tablaOcupadasBody.appendChild(tr);
        });
      })
      .catch((error) => {
        console.error("Error al cargar habitaciones ocupadas:", error);
        Swal.fire({
          icon: "error",
          title: "Error",
          text: "Error al cargar la información de habitaciones ocupadas.",
        });
      });
  }

  // Función para validar las fechas del formulario
  function validarFechas(fechaDesde, fechaHasta) {
    if (fechaDesde >= fechaHasta) {
      Swal.fire({
        icon: "warning",
        title: "Fechas inválidas",
        text: 'La fecha "Desde" debe ser anterior a la fecha "Hasta".',
      });
      return false;
    }
    return true;
  }

  // Manejo del envío del formulario de reserva
  reservationForm.addEventListener("submit", function (event) {
    event.preventDefault();
    const nombre = document.getElementById("nombre").value.trim();
    const apellido = document.getElementById("apellido").value.trim();
    const dni = document.getElementById("dni").value.trim();
    const fechaDesde = document.getElementById("fechaDesde").value;
    const fechaHasta = document.getElementById("fechaHasta").value;
    const idHabitacion = habitacionSelect.value;

    if (!idHabitacion) {
      Swal.fire({
        icon: "warning",
        title: "Atención",
        text: "Seleccione una habitación disponible.",
      });
      return;
    }
    if (!validarFechas(fechaDesde, fechaHasta)) return;

    const reservaDTO = {
      nombre: nombre,
      apellido: apellido,
      dni: dni,
      fechaDesde: fechaDesde,
      fechaHasta: fechaHasta,
      idHabitacion: Number(idHabitacion),
    };

    authedFetch("/api/reservas", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(reservaDTO),
    })
      .then((response) => {
        if (!response.ok) {
          return response.text().then((text) => {
            throw new Error(text);
          });
        }
        return response.json();
      })
      .then((data) => {
        Swal.fire({
          icon: "success",
          title: "Éxito",
          text: "Reserva realizada exitosamente.",
        });
        reservationForm.reset();
        cargarHabitacionesLibres();
        cargarHabitacionesOcupadas();
      })
      .catch((error) => {
        Swal.fire({
          icon: "error",
          title: "Error",
          text: error.message,
        });
      });
  });

  // Inicializar: cargar habitaciones libres y ocupadas al cargar la página
  cargarHabitacionesLibres();
  cargarHabitacionesOcupadas();
}
