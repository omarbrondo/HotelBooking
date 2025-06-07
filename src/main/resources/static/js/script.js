document.addEventListener("DOMContentLoaded", function () {
  const reservationForm = document.getElementById("reservationForm");
  const habitacionSelect = document.getElementById("habitacion");

  // Función para cargar las habitaciones libres en el select
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

  // Llamamos a la función para cargar las habitaciones al inicio
  cargarHabitacionesLibres();

  // Validación básica en el cliente antes de enviar el formulario
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

  // Manejo del envío del formulario
  reservationForm.addEventListener("submit", function (event) {
    event.preventDefault();

    // Recoger valores del formulario
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

    // Estructurar el objeto de la reserva a enviar
    const reservaDTO = {
      nombre: nombre,
      apellido: apellido,
      dni: dni,
      fechaDesde: fechaDesde,
      fechaHasta: fechaHasta,
      idHabitacion: Number(idHabitacion)
    };

    // Enviar la reserva al backend
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
      })
      .catch(error => {
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: error.message
        });
      });
  });
});
