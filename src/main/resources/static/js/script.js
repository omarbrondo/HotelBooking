document.addEventListener("DOMContentLoaded", function () {
  const reservationForm = document.getElementById("reservationForm");
  const habitacionSelect = document.getElementById("habitacion");
  const tablaOcupadasBody = document.querySelector("#tablaOcupadas tbody");

  // Función para cargar las habitaciones libres en el select del formulario
  function cargarHabitacionesLibres() {
    fetch("/api/habitaciones/libres")
      .then(response => response.json())
      .then(data => {
        habitacionSelect.innerHTML = `<option value="">Seleccione una habitación</option>`;
        data.forEach(habitacion => {
          const option = document.createElement("option");
          option.value = habitacion.idHabitacion;
          option.text = `${habitacion.nombreHabitacion} (Precio: $${habitacion.precio})`;
          habitacionSelect.appendChild(option);
        });
      })
      .catch(error => {
        console.error("Error al cargar habitaciones:", error);
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'Error al cargar las habitaciones disponibles.'
        });
      });
  }

  // Función para cargar las habitaciones ocupadas y sus datos de reserva
  function cargarHabitacionesOcupadas() {
    fetch("/api/habitaciones/ocupadas")
      .then(response => response.json())
      .then(data => {
        // Limpiar el tbody de la tabla
        tablaOcupadasBody.innerHTML = "";
        // Recorrer cada habitación ocupada
        data.forEach(habitacion => {
          const tr = document.createElement("tr");
          
          // Celda: Nombre de la habitación
          const tdHabitacion = document.createElement("td");
          tdHabitacion.textContent = habitacion.nombreHabitacion;
          tr.appendChild(tdHabitacion);
          
          // Celda: Precio
          const tdPrecio = document.createElement("td");
          tdPrecio.textContent = `$${habitacion.precio}`;
          tr.appendChild(tdPrecio);
          
          // Celda: Huésped (si existe reserva asociada)
          const tdHuesped = document.createElement("td");
          if (habitacion.reserva) {
            tdHuesped.textContent = `${habitacion.reserva.nombre} ${habitacion.reserva.apellido}`;
          } else {
            tdHuesped.textContent = "Sin reserva";
          }
          tr.appendChild(tdHuesped);
          
          // Celda: Fecha Desde
          const tdFechaDesde = document.createElement("td");
          tdFechaDesde.textContent = habitacion.reserva ? habitacion.reserva.fechaDesde : "";
          tr.appendChild(tdFechaDesde);
          
          // Celda: Fecha Hasta
          const tdFechaHasta = document.createElement("td");
          tdFechaHasta.textContent = habitacion.reserva ? habitacion.reserva.fechaHasta : "";
          tr.appendChild(tdFechaHasta);
          
          // Celda: Acciones
          const tdAcciones = document.createElement("td");
          // Aquí agregamos un botón de "Editar" (la funcionalidad se implementará más adelante)
          const btnEditar = document.createElement("button");
          btnEditar.textContent = "Editar";
          btnEditar.classList.add("btn-editar");
          // Agrega un event listener para iniciar el proceso de edición
          btnEditar.addEventListener("click", () => {
            // Por ahora mostramos un SweetAlert; luego se puede implementar el modal de edición
            Swal.fire({
              icon: 'info',
              title: 'Editar Reserva',
              text: 'Funcionalidad de edición pendiente de implementación.'
            });
          });
          tdAcciones.appendChild(btnEditar);
          tr.appendChild(tdAcciones);
  
          // Agregar la fila a la tabla
          tablaOcupadasBody.appendChild(tr);
        });
      })
      .catch(error => {
        console.error("Error al cargar habitaciones ocupadas:", error);
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'Error al cargar la información de habitaciones ocupadas.'
        });
      });
  }

  // Validación básica en el cliente para el formulario
  function validarFechas(fechaDesde, fechaHasta) {
    if (fechaDesde >= fechaHasta) {
      Swal.fire({
        icon: 'warning',
        title: 'Fechas inválidas',
        text: 'La fecha "Desde" debe ser anterior a la fecha "Hasta".'
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
        icon: 'warning',
        title: 'Atención',
        text: 'Seleccione una habitación disponible.'
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
      idHabitacion: Number(idHabitacion)
    };

    fetch("/api/reservas", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(reservaDTO)
    })
      .then(response => {
        if (!response.ok) {
          return response.text().then(text => { throw new Error(text) });
        }
        return response.json();
      })
      .then(data => {
        Swal.fire({
          icon: 'success',
          title: 'Éxito',
          text: 'Reserva realizada exitosamente.'
        });
        reservationForm.reset();
        cargarHabitacionesLibres();
        // Volvemos a cargar la tabla de ocupadas para reflejar la reserva ingresada
        cargarHabitacionesOcupadas();
      })
      .catch(error => {
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: error.message
        });
      });
  });

  // Cargar las habitaciones libres y ocupadas al iniciar la página
  cargarHabitacionesLibres();
  cargarHabitacionesOcupadas();
});
