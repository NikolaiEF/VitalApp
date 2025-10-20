package VitalApp.dto.medico;

import VitalApp.model.enums.Especialidad;
import VitalApp.model.vo.HorarioMedico;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import java.util.List;

public record EditarMedicoDTO(
        @NotBlank @Length(max = 36) String id,
        @NotBlank @Length(max = 100) String nombre,
        @NotBlank Especialidad especialidad,
        @NotBlank @Length(max = 100) String email,
        @NotNull List<HorarioMedico> horariosDisponibles
) {}