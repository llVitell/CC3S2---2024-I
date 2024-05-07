package microwave;
import java.util.List;

public class Microwave {
	private ModeController mc;
	private DisplayController dc;
	List<Preset> presets; 
	private int powerLevel; 

	private boolean doorOpen;
	private boolean COOKING;
	private ModeController.Mode mode = ModeController.Mode.SETUP;

	public Microwave(ModeController mc, DisplayController dc, 
			List<Preset> presets) {
		this.mc = mc;
		this.dc = dc;
		COOKING = false;
		doorOpen = true; // start in fail-safe state.
		this.presets = presets;
	}

	public void startPressed() { mc.setStartPressed(true); }
	public void clearPressed() { mc.setClearPressed(true); 
								 dc.clearKeyPressed(mc.getMode()); }

	public void digitPressed(int digit) {
		dc.digitPressed(digit);
	}
	
	public void presetPressed(int preset) {
		if (preset < 1 || preset > presets.size()) {
			throw new IllegalArgumentException("Preset out of range for presetPressed.");
		}
		if (mode != ModeController.Mode.SETUP) {
			throw new IllegalArgumentException("Presets can only be used in SETUP mode");
		}
		// Seeded fault here
		Preset p = presets.get(preset - 1);
		
		dc.setTimeToCook(p.getTimeToCook());
		setPowerLevel(p.getPowerLevel());
	}
		
	public void setDoorOpen(boolean doorOpen) { this.doorOpen = doorOpen; }
	
	public void setPowerLevel(int powerLevel) { 
		if (powerLevel >= 1 && powerLevel <= 10) {
			this.powerLevel = powerLevel; 
		} else {
			throw new IllegalArgumentException("power level out of range");
		}
	}
	
	public int getPowerLevel() {
		return this.powerLevel; 
	}
	
	public boolean isDoorOpen() {
		return doorOpen;
	}

	public void tick() {
		dc.tick(mode);
		mode = mc.tick(doorOpen, dc.timeToCook() != 0);
		COOKING = (mode == ModeController.Mode.COOKING);
	}
	
	public boolean isCOOKING() { return COOKING; }
	
	// should I make a copy of this?
	public byte [] digits() { return dc.getDigits(); }
	
	public ModeController.Mode getMode() { return mode; }
	
	public int getTickRateInHz() { return dc.getTickRateInHz(); }
	
	public void setTickRateInHz(int tickRate) { dc.setTickRateInHz(tickRate); }
}
