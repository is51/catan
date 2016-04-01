package catan.controllers.version;

class BuildInfoDetails {
    private DatabasePropertiesDetails databaseProps;
    private GitRepositoryStateDetails repositoryState;

    public BuildInfoDetails() {
    }

    public BuildInfoDetails(DatabasePropertiesDetails databaseProps, GitRepositoryStateDetails repositoryState) {
        this.databaseProps = databaseProps;
        this.repositoryState = repositoryState;
    }

    public GitRepositoryStateDetails getRepositoryState() {
        return repositoryState;
    }

    public void setRepositoryState(GitRepositoryStateDetails repositoryState) {
        this.repositoryState = repositoryState;
    }

    public DatabasePropertiesDetails getDatabaseProps() {
        return databaseProps;
    }

    public void setDatabaseProps(DatabasePropertiesDetails databaseProps) {
        this.databaseProps = databaseProps;
    }
}
