package VitalApp.dto.medico;

import VitalApp.model.enums.Especialidad;
import VitalApp.model.enums.jornadaMedico;

public record ItemMedicoDTO(
        String id,
        String nombre,
        String apellido,
        Especialidad especialidad,
        String correo,
        jornadaMedico jornadaMedico) {}