package VitalApp.model.documents;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("paciente")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Paciente {
    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String rh;
    private String fechaNacimiento;
    private String direccion;
}
