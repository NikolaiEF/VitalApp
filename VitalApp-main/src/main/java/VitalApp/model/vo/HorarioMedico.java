package VitalApp.model.vo;

import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class HorarioMedico {

    private DayOfWeek diaSemana; // opcional, puedes usarlo si la jornada cambia por días
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private boolean reservado;
}
