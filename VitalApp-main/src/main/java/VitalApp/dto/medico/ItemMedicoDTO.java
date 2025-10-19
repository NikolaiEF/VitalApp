package VitalApp.dto.medico;

import VitalApp.model.enums.jornadaMedico;

public record ItemMedicoDTO(
        String id,
        String nombre,
        String apellido,
        String especialidad,
        String correo,
        jornadaMedico jornadaMedico) {}