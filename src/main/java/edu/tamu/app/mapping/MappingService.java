package edu.tamu.app.mapping;

import edu.tamu.app.model.ServiceMapping;

public interface MappingService<C, T extends ServiceMapping<C>> {

    public C map(String mapping);

    public C handleUnmapped(String mapping);

}
