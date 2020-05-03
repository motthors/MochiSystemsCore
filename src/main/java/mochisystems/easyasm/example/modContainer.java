package mochisystems.easyasm.example;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.versioning.ArtifactVersion;

public class modContainer extends DummyModContainer {
	
	private String annotationDependencies; 
	private Map<String, Object> descriptor = new HashMap<>();
	
	public modContainer() {
		super(new ModMetadata());

		ModMetadata meta = super.getMetadata();
		meta.modId = "mochisystems.easyasm";
		meta.name = "easyASM";
		meta.version = "0.1";
		meta.authorList = Arrays.asList(new String[] { "MOTTY" });
		meta.useDependencyInformation = true;
		
		Set<ArtifactVersion> requirements = Sets.newHashSet();
        List<ArtifactVersion> dependencies = Lists.newArrayList();
        List<ArtifactVersion> dependants = Lists.newArrayList();
        annotationDependencies = (String) descriptor.get("dependencies");
//        annotationDependencies = "after:XXXmod";
        Loader.instance().computeDependencies(annotationDependencies, requirements, dependencies, dependants);
        meta.requiredMods = requirements;
        meta.dependencies = dependencies;
        meta.dependants = dependants;

//        meta.dependencies.add(VersionParser.parseVersionReference("after:shadersmod"));
        
		this.setEnabledState(true);
	}
	
	@Override
	public boolean registerBus(com.google.common.eventbus.EventBus bus, LoadController lc) {
		bus.register(this);
		return true;
	}

}
