package controller;

public enum Role {
	ADMIN_SISTEMA(1),
	ADMIN_DOKUMENTA(2),
	KLIJENT(3);
	
	private final int id;
    Role(int id) { this.id = id; }
    public int getValue() { return id; }
}
