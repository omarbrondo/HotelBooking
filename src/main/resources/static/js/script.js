document.addEventListener("DOMContentLoaded", function () {
  const reservationForm = document.getElementById("reservationForm");
  const habitacionSelect = document.getElementById("habitacion");
  const tablaOcupadasBody = document.querySelector("#tablaOcupadas tbody");

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

  // Función para cargar las habitaciones ocupadas y construir la tabla
  function cargarHabitacionesOcupadas() {
    fetch("/api/habitaciones/ocupadas")
      .then(response => response.json())
      .then(data => {
        tablaOcupadasBody.innerHTML = "";
        data.forEach(habitacion => {
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
          tdFechaDesde.textContent = habitacion.reserva ? habitacion.reserva.fechaDesde : "";
          tr.appendChild(tdFechaDesde);

          // Columna: Fecha Hasta
          const tdFechaHasta = document.createElement("td");
          tdFechaHasta.textContent = habitacion.reserva ? habitacion.reserva.fechaHasta : "";
          tr.appendChild(tdFechaHasta);

          // Columna: Acciones
          const tdAcciones = document.createElement("td");

          // Botón "Editar" (Bootstrap: btn y btn-primary, btn-sm)
          const btnEditar = document.createElement("button");
          btnEditar.textContent = "Editar";
          btnEditar.classList.add("btn", "btn-primary", "btn-sm");
          btnEditar.addEventListener("click", () => {
            if (!habitacion.reserva) {
              Swal.fire({
                icon: 'info',
                title: 'Sin reserva',
                text: 'No hay datos para editar en esta habitación.'
              });
              return;
            }
            Swal.fire({
              title: 'Editar Reserva',
              html:
                '<input id="swal-input1" class="swal2-input" placeholder="Nombre" value="' + habitacion.reserva.nombre + '">' +
                '<input id="swal-input2" class="swal2-input" placeholder="Apellido" value="' + habitacion.reserva.apellido + '">' +
                '<input id="swal-input3" class="swal2-input" placeholder="DNI" value="' + habitacion.reserva.dni + '">' +
                '<input id="swal-input4" type="date" class="swal2-input" value="' + habitacion.reserva.fechaDesde + '">' +
                '<input id="swal-input5" type="date" class="swal2-input" value="' + habitacion.reserva.fechaHasta + '">',
              focusConfirm: false,
              preConfirm: () => {
                return {
                  nombre: document.getElementById('swal-input1').value,
                  apellido: document.getElementById('swal-input2').value,
                  dni: document.getElementById('swal-input3').value,
                  fechaDesde: document.getElementById('swal-input4').value,
                  fechaHasta: document.getElementById('swal-input5').value
                };
              }
            }).then(result => {
              if (result.isConfirmed) {
                const updatedData = result.value;
                fetch('/api/reservas/' + habitacion.reserva.idReserva, {
                  method: 'PUT',
                  headers: {
                    "Content-Type": "application/json"
                  },
                  body: JSON.stringify(updatedData)
                })
                .then(response => {
                  if (!response.ok) {
                    return response.text().then(text => { throw new Error(text); });
                  }
                  return response.json();
                })
                .then(data => {
                  Swal.fire({
                    icon: 'success',
                    title: '¡Actualizado!',
                    text: 'La reserva se actualizó correctamente.'
                  });
                  cargarHabitacionesOcupadas();
                })
                .catch(error => {
                  Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: error.message
                  });
                });
              }
            });
          });
          tdAcciones.appendChild(btnEditar);

          // Botón "Agregar productos" (Bootstrap: btn y btn-success, btn-sm)
          const btnAgregarProductos = document.createElement("button");
          btnAgregarProductos.textContent = "Agregar productos";
          btnAgregarProductos.classList.add("btn", "btn-success", "btn-sm");
          btnAgregarProductos.style.marginLeft = "5px";
          btnAgregarProductos.addEventListener("click", () => {
            if (!habitacion.reserva) {
              Swal.fire({
                icon: 'info',
                title: 'Sin reserva',
                text: 'No hay reserva asociada para agregar productos.'
              });
              return;
            }
            fetch("/api/productos")
              .then(resp => resp.json())
              .then(productos => {
                let htmlProductos = '<table class="table table-striped" style="text-align:center;">' +
                  '<thead>' +
                    '<tr>' +
                      '<th>Producto</th>' +
                      '<th>Precio</th>' +
                      '<th>Cantidad</th>' +
                    '</tr>' +
                  '</thead>' +
                  '<tbody>';
                productos.forEach(prod => {
                  htmlProductos +=
                    '<tr>' +
                      `<td>${prod.nombreProducto}</td>` +
                      `<td>$${prod.precio}</td>` +
                      `<td><input type="number" min="0" value="0" id="cantidad-${prod.idProducto}" style="width:50px;" /></td>` +
                    '</tr>';
                });
                htmlProductos += '</tbody></table>';
    
                Swal.fire({
                  title: 'Agregar Productos',
                  html: htmlProductos,
                  showCancelButton: true,
                  confirmButtonText: 'Guardar',
                  preConfirm: () => {
                    const consumos = [];
                    productos.forEach(prod => {
                      const cantidad = document.getElementById(`cantidad-${prod.idProducto}`).value;
                      const cant = parseInt(cantidad);
                      if (cant > 0) {
                        consumos.push({
                          idProducto: prod.idProducto,
                          cantidad: cant
                        });
                      }
                    });
                    if (consumos.length === 0) {
                      Swal.showValidationMessage("Debes seleccionar al menos un producto con cantidad mayor a 0");
                    }
                    return consumos;
                  }
                }).then(result => {
                  if (result.isConfirmed) {
                    const consumos = result.value;
                    const reservaId = habitacion.reserva.idReserva;
                    Promise.all(consumos.map(consumo =>
                      fetch(`/api/reservas/${reservaId}/consumos`, {
                        method: 'POST',
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify(consumo)
                      })
                    ))
                    .then(() => {
                      Swal.fire({
                        icon: 'success',
                        title: 'Guardado',
                        text: 'Productos agregados a la reserva'
                      });
                      cargarHabitacionesOcupadas();
                    })
                    .catch(error => {
                      Swal.fire({
                        icon: 'error',
                        title: 'Error',
                        text: error.message
                      });
                    });
                  }
                });
              })
              .catch(error => {
                Swal.fire({
                  icon: 'error',
                  title: 'Error',
                  text: 'No se pudo cargar los productos.'
                });
              });
          });
          tdAcciones.appendChild(btnAgregarProductos);

          // Botón "Detalle" (Bootstrap: btn y btn-warning, btn-sm)
          const btnDetalle = document.createElement("button");
          btnDetalle.textContent = "Detalle";
          btnDetalle.classList.add("btn", "btn-warning", "btn-sm");
          btnDetalle.style.marginLeft = "5px";
          btnDetalle.addEventListener("click", () => {
            if (!habitacion.reserva) {
              Swal.fire({
                icon: 'info',
                title: 'Sin reserva',
                text: 'No hay datos de reserva para mostrar.'
              });
              return;
            }
            fetch(`/api/reservas/${habitacion.reserva.idReserva}`)
              .then(response => response.json())
              .then(detalle => {
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
                  detalle.consumos.forEach(consumo => {
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
                  title: 'Detalle de Reserva',
                  html: htmlDetalle,
                  width: '600px'
                });
              })
              .catch(error => {
                Swal.fire({
                  icon: 'error',
                  title: 'Error',
                  text: 'No se pudo cargar el detalle de la reserva.'
                });
              });
          });
          tdAcciones.appendChild(btnDetalle);

          // Botón "Eliminar" (Bootstrap: btn y btn-danger, btn-sm)
          const btnEliminar = document.createElement("button");
          btnEliminar.textContent = "Eliminar";
          btnEliminar.classList.add("btn", "btn-danger", "btn-sm");
          btnEliminar.style.marginLeft = "5px";
          btnEliminar.addEventListener("click", () => {
            if (!habitacion.reserva) {
              Swal.fire({
                icon: 'info',
                title: 'Sin reserva',
                text: 'No hay reserva para eliminar.'
              });
              return;
            }
            Swal.fire({
              title: '¿Estás seguro?',
              text: "Se eliminará la reserva, sus consumos y la habitación quedará libre.",
              icon: 'warning',
              showCancelButton: true,
              confirmButtonText: 'Sí, eliminar',
              cancelButtonText: 'Cancelar'
            }).then(result => {
              if (result.isConfirmed) {
                fetch(`/api/reservas/${habitacion.reserva.idReserva}`, {
                  method: 'DELETE'
                })
                .then(response => {
                  if (response.ok) {
                    Swal.fire({
                      icon: 'success',
                      title: 'Eliminado',
                      text: 'La reserva se eliminó correctamente.'
                    });
                    cargarHabitacionesOcupadas();
                    cargarHabitacionesLibres();
                  } else {
                    return response.text().then(text => { throw new Error(text); });
                  }
                })
                .catch(error => {
                  Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: error.message
                  });
                });
              }
            });
          });
          tdAcciones.appendChild(btnEliminar);

          tr.appendChild(tdAcciones);
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

  // Función para validar las fechas del formulario
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
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(reservaDTO)
    })
      .then(response => {
        if (!response.ok) {
          return response.text().then(text => { throw new Error(text); });
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

  // Inicializar: cargar habitaciones libres y ocupadas al cargar la página
  cargarHabitacionesLibres();
  cargarHabitacionesOcupadas();
});
