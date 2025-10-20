package VitalApp.dto.medico;

import VitalApp.model.enums.Especialidad;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record InformacionMedicoDTO(
        String id,
        String nombre,
        Especialidad especialidad
) {}