
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Entity 
@Data 
public class prova {
    @NotEmpty 
    @Column(unique=true)
    String nome;

    @Max(20)
    int a;



}
