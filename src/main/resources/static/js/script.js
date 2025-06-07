document.addEventListener("DOMContentLoaded", function () {
  const reservationForm = document.getElementById("reservationForm");
  const habitacionSelect = document.getElementById("habitacion");
  const messageDiv = document.getElementById("message");

  // Función para cargar las habitaciones libres en el select
  function cargarHabitacionesLibres() {
    fetch("/api/habitaciones/libres")
      .then(response => response.json())
      .then(data => {
        habitacionSelect.innerHTML = `<option value="">Seleccione una habitación</option>`;
        data.forEach(habitacion => {
          // Puedes personalizar el texto mostrado, por ejemplo: nombre y precio
          const option = document.createElement("option");
          option.value = habitacion.idHabitacion;
          option.text = `${habitacion.nombreHabitacion} (Precio: $${habitacion.precio})`;
          habitacionSelect.appendChild(option);
        });
      })
      .catch(error => {
        console.error("Error al cargar habitaciones:", error);
        messageDiv.textContent = "Error al cargar las habitaciones disponibles.";
      });
  }

  // Llamamos a la función para cargar las habitaciones al inicio
  cargarHabitacionesLibres();

  // Manejo del envío del formulario
  reservationForm.addEventListener("submit", function (event) {
    event.preventDefault();

    // Recoger valores del formulario
    const nombre = document.getElementById("nombre").value;
    const apellido = document.getElementById("apellido").value;
    const dni = document.getElementById("dni").value;
    const fechaDesde = document.getElementById("fechaDesde").value;
    const fechaHasta = document.getElementById("fechaHasta").value;
    const idHabitacion = habitacionSelect.value;

    if (!idHabitacion) {
      messageDiv.textContent = "Seleccione una habitación disponible.";
      return;
    }

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
        messageDiv.style.color = "green";
        messageDiv.textContent = "Reserva realizada exitosamente.";
        // Vaciamos el formulario y recargamos la lista de habitaciones disponibles
        reservationForm.reset();
        cargarHabitacionesLibres();
      })
      .catch(error => {
        messageDiv.style.color = "red";
        messageDiv.textContent = "Error: " + error.message;
      });
  });
});
