import React, { useState, useEffect } from "react";
import ApiService from "../../service/ApiService";
import "../../styles/manage-bookings.css";
import "../../styles/manage-bookings-popup.css";

const ManageBookingsPage = () => {
  const [bookings, setBookings] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Control del modal de gestión de la reserva
  const [selectedBooking, setSelectedBooking] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  // Formulario de edición
  const [paymentStatus, setPaymentStatus] = useState("");
  const [bookingStatus, setBookingStatus] = useState("");

  useEffect(() => {
    fetchBookings();
  }, []);

     const fetchBookings = async () => {
    try {
      const data = await ApiService.getAllBookings();
      
      console.log("Respuesta cruda del servidor:", data);

      // 🎯 Corrección: Accedemos directamente a la propiedad 'bookings' que envía tu Spring Boot
      if (data && Array.isArray(data.bookings)) {
        setBookings(data.bookings);
      } else if (Array.isArray(data)) {
        setBookings(data);
      } else if (data && Array.isArray(data.bookingDTOList)) {
        setBookings(data.bookingDTOList);
      } else if (data && Array.isArray(data.bookingList)) {
        setBookings(data.bookingList);
      } else {
        console.warn("No se encontró la propiedad 'bookings' en la respuesta del servidor.");
        setBookings([]);
      }
      setLoading(false);
    } catch (err) {
      setError(err.message || "Error al cargar las reservas.");
      setLoading(false);
    }
  };

  const handleManageClick = (booking, refCode, email) => {
    setSelectedBooking({
      ...booking,
      // Guardamos explícitamente el código real que usará el backend
      bookingReference: booking.bookingReference || booking.bookingReferenceCode || refCode,
      derivedRef: refCode,
      derivedEmail: email,
    });
    setPaymentStatus(booking.paymentStatus || "PENDING");
    setBookingStatus(booking.bookingStatus || "BOOKED");
    setIsModalOpen(true);
  };

  const handleSaveChanges = async () => {
    try {
      // 🧱 Construimos la maleta con la estructura exacta que exige Java:
      const updatedBooking = {
        id: selectedBooking.id, // ID de la reserva necesario para el UPDATE
        bookingReference: selectedBooking.bookingReference, // 👈 Nombre exacto del DTO
        bookingStatus: bookingStatus,
        paymentStatus: paymentStatus,
        checkInDate: selectedBooking.checkInDate,   // Conserva las fechas existentes
        checkOutDate: selectedBooking.checkOutDate, // Conserva las fechas existentes
        numOfGuests: selectedBooking.numOfGuests || 1, // Evita mandar nulos obligatorios
        // Si el backend te pide explícitamente el ID de habitación a nivel raíz:
        roomId: selectedBooking.roomId || (selectedBooking.room && selectedBooking.room.id)
      };

      // Enviamos el DTO limpio a la API
      await ApiService.updateBooking(updatedBooking);
      
      setIsModalOpen(false);
      fetchBookings();
      alert("Reserva actualizada correctamente");
    } catch (err) {
      alert("Error al actualizar la reserva: " + err.message);
    }
  };

  const filteredBookings = bookings.filter((booking) => {
    // 👇 Corrección: Buscamos sobre la variable exacta de tu Java DTO
    const refCode = booking.bookingReference || "";
    const email =
      booking.customerEmail || (booking.user && booking.user.email) || "";
    return (
      refCode.toLowerCase().includes(searchTerm.toLowerCase()) ||
      email.toLowerCase().includes(searchTerm.toLowerCase())
    );
  });

  if (loading)
    return (
      <div className="gbh-loading-screen">
        <p>Cargando conserjería...</p>
      </div>
    );

  return (
    <div className="gbh-bookings-page-container">
      <div className="gbh-bookings-panel-wrapper">
        <div className="gbh-bookings-main-card">
          {/* Sección Superior: Título y Buscador */}
          <div className="gbh-bookings-top-bar">
            <h2 className="gbh-bookings-main-title">
              🗃️ Registro General de Reservas
            </h2>
            <input
              type="text"
              placeholder="Buscar por código o email..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="gbh-bookings-search-input"
            />
          </div>

          {error && <p className="gbh-bookings-error-msg">{error}</p>}

          {/* Tabla de Resultados */}
          <div className="gbh-bookings-table-section">
            <div className="gbh-bookings-table-scroll">
              <table className="gbh-custom-data-table">
                <thead>
                  <tr>
                    <th>Código Referencia</th>
                    <th>Email Cliente</th>
                    <th>Check-In</th>
                    <th>Check-Out</th>
                    <th>Estado</th>
                    <th>Acción</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredBookings.map((booking, index) => {
                    const refCode =
                      booking.bookingReferenceCode ||
                      booking.bookingConfirmationCode ||
                      "REG-" + (booking.id || index);
                    const email =
                      booking.customerEmail ||
                      (booking.user && booking.user.email) ||
                      "invitado@grandbudapest.com";

                    return (
                      <tr key={booking.id || index} className="gbh-table-row">
                        <td className="gbh-td-code">{refCode}</td>
                        <td>{email}</td>
                        <td>{booking.checkInDate}</td>
                        <td>{booking.checkOutDate}</td>
                        <td>
                          <span
                            className={`gbh-status-badge ${booking.bookingStatus === "BOOKED" ? "gbh-status-booked" : "gbh-status-cancelled"}`}
                          >
                            {booking.bookingStatus || "BOOKED"}
                          </span>
                        </td>
                        <td>
                          <button
                            type="button"
                            onClick={() =>
                              handleManageClick(booking, refCode, email)
                            }
                            className="gbh-btn-manage-action"
                          >
                            Gestionar
                          </button>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          </div>

          {/* Popup o Modal de Gestión a nivel Reserva */}
          {isModalOpen && selectedBooking && (
            <div className="gbh-popup-overlay">
              <div className="gbh-popup-box">
                <h3 className="gbh-popup-title">📋 Panel de Gestión de Reserva</h3>

                {/* SECCIÓN 1: IDENTIFICADORES CLAVE */}
                <div className="gbh-popup-section-title">🔑 Identificación</div>
                <div className="gbh-popup-form-grid">
                  <div className="gbh-popup-field">
                    <label>Código Referencia</label>
                    <input
                      type="text"
                      value={selectedBooking.bookingReference || selectedBooking.derivedRef || "N/A"}
                      disabled
                      className="gbh-popup-input-disabled"
                    />
                  </div>
                  <div className="gbh-popup-field">
                    <label>ID de Reserva (Database)</label>
                    <input
                      type="text"
                      value={selectedBooking.id || "N/A"}
                      disabled
                      className="gbh-popup-input-disabled"
                    />
                  </div>
                </div>

                {/* SECCIÓN 2: DATOS DEL CLIENTE / HUÉSPED */}
                <div className="gbh-popup-section-title">👤 Datos del Huésped</div>
                <div className="gbh-popup-form-grid">
                  <div className="gbh-popup-field">
                    <label>Nombre Completo</label>
                    <input
                      type="text"
                      value={`${selectedBooking.user?.firstName || ''} ${selectedBooking.user?.lastName || ''}`.trim() || selectedBooking.customerName || "Invitado"}
                      disabled
                      className="gbh-popup-input-disabled"
                    />
                  </div>
                  <div className="gbh-popup-field">
                    <label>Email de Contacto</label>
                    <input
                      type="text"
                      value={selectedBooking.user?.email || selectedBooking.customerEmail || selectedBooking.derivedEmail}
                      disabled
                      className="gbh-popup-input-disabled"
                    />
                  </div>
                  <div className="gbh-popup-field">
                    <label>Teléfono</label>
                    <input
                      type="text"
                      value={selectedBooking.user?.phoneNumber || selectedBooking.customerPhone || "No registrado"}
                      disabled
                      className="gbh-popup-input-disabled"
                    />
                  </div>
                  <div className="gbh-popup-field">
                    <label>Total Huéspedes</label>
                    <input
                      type="text"
                      value={selectedBooking.numOfGuests || "1"}
                      disabled
                      className="gbh-popup-input-disabled"
                    />
                  </div>
                </div>

                                {/* SECCIÓN 3: DETALLES DE LA HABITACIÓN Y FECHAS */}
                <h4 className="gbh-popup-section-title">🏨 Habitación y Estancia</h4>
                <div className="gbh-popup-form-grid">
                  <div className="gbh-popup-field">
                    <label>Número / Tipo de Habitación</label>
                    <input
                      type="text"
                      value={
                        selectedBooking.room 
                          ? `Nº ${selectedBooking.room.roomNumber} - ${selectedBooking.room.roomType || selectedBooking.room.type || "Asignada"}` 
                          : "No asignada"
                      }
                      disabled
                      className="gbh-popup-input-disabled"
                    />
                  </div>
                  <div className="gbh-popup-field">
                    <label>Precio por Noche</label>
                    <input
                      type="text"
                      value={
                        selectedBooking.room?.roomPrice || selectedBooking.room?.pricePerNight
                          ? `${selectedBooking.room.roomPrice || selectedBooking.room.pricePerNight} USD` 
                          : "N/A"
                      }
                      disabled
                      className="gbh-popup-input-disabled"
                    />
                  </div>
                  <div className="gbh-popup-field">
                    <label>Fecha Check-In</label>
                    <input
                      type="text"
                      value={selectedBooking.checkInDate}
                      disabled
                      className="gbh-popup-input-disabled"
                    />
                  </div>
                  <div className="gbh-popup-field">
                    <label>Fecha Check-Out</label>
                    <input
                      type="text"
                      value={selectedBooking.checkOutDate}
                      disabled
                      className="gbh-popup-input-disabled"
                    />
                  </div>
                </div>


                {/* SECCIÓN 4: ACCIONES DE ADMINISTRACIÓN */}
                <div className="gbh-popup-section-title">⚙️ Control de Estado</div>
                <div className="gbh-popup-form-grid">
                  <div className="gbh-popup-field">
                    <label>Estado de la Reserva</label>
                    <select
                      value={bookingStatus}
                      onChange={(e) => setBookingStatus(e.target.value)}
                      className="gbh-popup-select-control"
                    >
                      <option value="BOOKED">🟢 BOOKED (Confirmada)</option>
                      <option value="CANCELLED">🔴 CANCELLED (Cancelada)</option>
                    </select>
                  </div>
                  <div className="gbh-popup-field">
                    <label>Estado del Pago</label>
                    <select
                      value={paymentStatus}
                      onChange={(e) => setPaymentStatus(e.target.value)}
                      className="gbh-popup-select-control"
                    >
                      <option value="PENDING">🟡 PENDING (Pendiente)</option>
                      <option value="PAID">🟢 PAID (Pagado)</option>
                      <option value="FAILED">🔴 FAILED (Fallido)</option>
                    </select>
                  </div>
                </div>

                {/* BOTONERA */}
                <div className="gbh-popup-buttons-row" style={{ marginTop: "24px" }}>
                  <button
                    type="button"
                    onClick={handleSaveChanges}
                    className="gbh-popup-btn-save"
                  >
                    Guardar Cambios del Sistema
                  </button>
                  <button
                    type="button"
                    onClick={() => setIsModalOpen(false)}
                    className="gbh-popup-btn-close"
                  >
                    Cerrar Ventana
                  </button>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ManageBookingsPage;


