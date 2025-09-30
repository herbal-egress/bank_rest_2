package com.example.bankcards.entity;

// Импорт классов для шифрования, пользователя и аннотации ManyToOne.
import com.example.bankcards.util.EncryptionConverter; // изменил ИИ: добавил импорт для конвертера атрибутов JPA, чтобы перенести логику шифрования из сущности в конвертер (соответствие SOLID: SRP - сущность не отвечает за шифрование; OWASP: централизованная обработка чувствительных данных).
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "cards", schema = "bankrest")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 16, max = 16, message = "Номер карты должен содержать 16 цифр")
    @Column(name = "number", nullable = false)
    @Convert(converter = EncryptionConverter.class) // изменил ИИ: добавил аннотацию @Convert для автоматического шифрования/дешифрования номера карты при взаимодействии с БД (соответствие ООП: инкапсуляция; SOLID: DIP - зависимость от абстракции конвертера; OWASP: автоматическая защита данных в покое).
    private String number;

    @NotNull
    @Size(max = 50, message = "Имя на карте не должно превышать 50 символов")
    @Pattern(regexp = "^[A-Za-z]+\\s[A-Za-z]+$", message = "Имя должно содержать только буквы и пробел между ними")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Size(min = 5, max = 5, message = "Срок действия должен быть в формате MM-YY")
    @Pattern(regexp = "^(0[1-9]|1[0-2])-\\d{2}$", message = "Срок действия должен быть в формате 01-12 и содержать 2 цифры года")
    @Column(name = "expiration", nullable = false)
    private String expiration;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CardStatus status;

    @NotNull
    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    private double balance;

    @NotNull
    @Size(min = 3, max = 3, message = "CVV должен содержать 3 цифры")
    @Pattern(regexp = "^\\d{3}$", message = "CVV должен содержать только 3 цифры")
    @Column(name = "cvv", nullable = false)
    private String cvv;

    // Связь с пользователем через поле userId. Аннотация ManyToOne указывает на связь "многие к одному" с сущностью User.
    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Card() {
    }

    public Card(String number, String name, String expiration, CardStatus status, double balance, String cvv, User user) {
        this.number = number; // изменил ИИ: удалил вызов EncryptionUtil.encrypt, так как шифрование теперь обрабатывается конвертером (соответствие SOLID: OCP - изменения без модификации сущности).
        this.name = name;
        this.expiration = expiration;
        this.status = status;
        this.balance = balance;
        this.cvv = cvv;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number; // изменил ИИ: удалил вызов EncryptionUtil.decrypt, так как дешифрование теперь обрабатывается конвертером (соответствие OWASP: минимизация ручной обработки чувствительных данных).
    }

    public void setNumber(String number) {
        this.number = number; // изменил ИИ: удалил вызов EncryptionUtil.encrypt, так как шифрование теперь обрабатывается конвертером.
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public CardStatus getStatus() {
        return status;
    }

    public void setStatus(CardStatus status) {
        this.status = status;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}