package modularforcefields.registers;

import modularforcefields.References;
import modularforcefields.common.inventory.container.ContainerBiometricIdentifier;
import modularforcefields.common.inventory.container.ContainerCoercionDeriver;
import modularforcefields.common.inventory.container.ContainerFortronCapacitor;
import modularforcefields.common.inventory.container.ContainerFortronFieldProjector;
import modularforcefields.common.inventory.container.ContainerInterdictionMatrix;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModularForcefieldsMenuTypes {
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, References.ID);

	public static final RegistryObject<MenuType<ContainerCoercionDeriver>> CONTAINER_COERCIONDERIVER = MENU_TYPES.register("coercionderiver", () -> new MenuType<>(ContainerCoercionDeriver::new));
	public static final RegistryObject<MenuType<ContainerFortronCapacitor>> CONTAINER_FORTRONCAPACITOR = MENU_TYPES.register("fortroncapacitor", () -> new MenuType<>(ContainerFortronCapacitor::new));
	public static final RegistryObject<MenuType<ContainerFortronFieldProjector>> CONTAINER_FORTRONFIELDPROJECTOR = MENU_TYPES.register("fortronfieldprojector", () -> new MenuType<>(ContainerFortronFieldProjector::new));
	public static final RegistryObject<MenuType<ContainerInterdictionMatrix>> CONTAINER_INTERDICTIONMATRIX = MENU_TYPES.register("interdictionmatrix", () -> new MenuType<>(ContainerInterdictionMatrix::new));
	public static final RegistryObject<MenuType<ContainerBiometricIdentifier>> CONTAINER_BIOMETRICIDENTIFIER = MENU_TYPES.register("biometricidentifier", () -> new MenuType<>(ContainerBiometricIdentifier::new));

}
