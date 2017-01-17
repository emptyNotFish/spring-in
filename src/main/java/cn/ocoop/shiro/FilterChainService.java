package cn.ocoop.shiro;

import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;

import javax.servlet.Filter;
import java.util.List;
import java.util.Map;

public abstract class FilterChainService {
    public static DefaultFilterChainManager filterChainManager;

    public final void createFilterChains(DefaultFilterChainManager filterChainManager) {
        FilterChainService.filterChainManager = filterChainManager;
        filterChainManager.getFilterChains().clear();
        List<NamedChainDefinition> namedChainDefinitions = getNamedChainDefinitions();
        if (CollectionUtils.isEmpty(namedChainDefinitions)) return;
        for (NamedChainDefinition namedChainDefinition : namedChainDefinitions) {
            createChain(namedChainDefinition.getChainName(), namedChainDefinition.getChainDefinition());
        }
    }


    protected void createChain(String chainName, String chainDefinition) {
        filterChainManager.createChain(chainName, chainDefinition);
    }

    protected abstract List<NamedChainDefinition> getNamedChainDefinitions();

    public Map<String, Filter> getFilters() {
        return null;
    }

    public static class NamedChainDefinition {
        private String chainName;
        private String chainDefinition;

        public NamedChainDefinition() {
        }

        public NamedChainDefinition(String chainName, String chainDefinition) {
            this.chainName = chainName;
            this.chainDefinition = chainDefinition;
        }

        public String getChainName() {
            return chainName;
        }

        public void setChainName(String chainName) {
            this.chainName = chainName;
        }

        public String getChainDefinition() {
            return chainDefinition;
        }

        public void setChainDefinition(String chainDefinition) {
            this.chainDefinition = chainDefinition;
        }
    }
}
