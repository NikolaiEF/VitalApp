package VitalApp.dto.medico;

import VitalApp.model.enums.jornadaMedico;
import VitalApp.model.vo.HorarioMedico;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public record CrearMedicoDTO(
        @NotBlank @Length(max = 100) String id,
        @NotBlank @Length(max = 100) String nombre,
        @NotBlank @Length(max = 100) String apellido,
        @NotBlank @Length(max = 50) String especialidad,
        @NotBlank @Length(max = 100) String correo,
        @NotNull jornadaMedico jornadaMedico
) {}