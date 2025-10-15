package VitalApp.service.util;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void enviarCorreo(String para, String asunto, String cuerpo) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(para);
        mensaje.setSubject(asunto);
        mensaje.setText(cuerpo);
        mensaje.setFrom("no-reply@vitalapp.com"); // opcional
        mailSender.send(mensaje);
    }

    public void enviarBienvenida(String email, String nombre, boolean esMedico) {
        String tipo = esMedico ? "doctor" : "paciente";
        String asunto = "¡Bienvenido a VitalApp!";
        String cuerpo = String.format(
                "Hola %s,\n\nGracias por registrarte como %s en VitalApp.\n" +
                        "Ahora podrás gestionar tus citas fácilmente.\n\nSaludos,\nEl equipo de VitalApp",
                nombre, tipo
        );
        enviarCorreo(email, asunto, cuerpo);
    }

    public void enviarConfirmacionCita(String email, String nombre, String fecha, String hora) {
        String asunto = "Confirmación de cita médica";
        String cuerpo = String.format(
                "Hola %s,\n\nTu cita ha sido agendada para el día %s a las %s.\n" +
                        "Gracias por confiar en VitalApp.\n\nSaludos,\nEl equipo de VitalApp",
                nombre, fecha, hora
        );
        enviarCorreo(email, asunto, cuerpo);
    }
}
