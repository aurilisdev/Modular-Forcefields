package modularforcefields.registers;

import modularforcefields.References;
import modularforcefields.common.inventory.container.ContainerBiometricIdentifier;
import modularforcefields.common.inventory.container.ContainerCoercionDeriver;
import modularforcefields.common.inventory.container.ContainerFortronCapacitor;
import modularforcefields.common.inventory.container.ContainerFortronFieldProjector;
import modularforcefields.common.inventory.container.ContainerInterdictionMatrix;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.MenuType.MenuSupplier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModularForcefieldsMenuTypes {
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, References.ID);

	public static final RegistryObject<MenuType<ContainerCoercionDeriver>> CONTAINER_COERCIONDERIVER = register("coercionderiver", ContainerCoercionDeriver::new);
	public static final RegistryObject<MenuType<ContainerFortronCapacitor>> CONTAINER_FORTRONCAPACITOR = register("fortroncapacitor", ContainerFortronCapacitor::new);
	public static final RegistryObject<MenuType<ContainerFortronFieldProjector>> CONTAINER_FORTRONFIELDPROJECTOR = register("fortronfieldprojector", ContainerFortronFieldProjector::new);
	public static final RegistryObject<MenuType<ContainerInterdictionMatrix>> CONTAINER_INTERDICTIONMATRIX = register("interdictionmatrix", ContainerInterdictionMatrix::new);
	public static final RegistryObject<MenuType<ContainerBiometricIdentifier>> CONTAINER_BIOMETRICIDENTIFIER = register("biometricidentifier", ContainerBiometricIdentifier::new);

	private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String id, MenuSupplier<T> supplier) {
		return MENU_TYPES.register(id, () -> new MenuType<>(supplier, FeatureFlags.VANILLA_SET));
	}
}
