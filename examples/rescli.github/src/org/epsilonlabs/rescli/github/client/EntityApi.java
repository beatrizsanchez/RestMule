package org.epsilonlabs.rescli.github.client;

import static org.epsilonlabs.rescli.core.util.PropertiesUtil.API_BASE_URL;

import java.util.concurrent.ExecutorService;

import org.epsilonlabs.rescli.core.client.AbstractClient;
import org.epsilonlabs.rescli.core.client.IClientBuilder;
import org.epsilonlabs.rescli.core.data.Data;
import org.epsilonlabs.rescli.core.data.IData;
import org.epsilonlabs.rescli.core.data.IDataSet;
import org.epsilonlabs.rescli.core.session.ISession;
import org.epsilonlabs.rescli.core.session.RateLimitExecutor;
import org.epsilonlabs.rescli.github.interceptor.GitHubInterceptor;
import org.epsilonlabs.rescli.github.model.Asset;
import org.epsilonlabs.rescli.github.model.Assets;
import org.epsilonlabs.rescli.github.model.Assignees;
import org.epsilonlabs.rescli.github.model.Blob;
import org.epsilonlabs.rescli.github.model.Branch;
import org.epsilonlabs.rescli.github.model.Branches;
import org.epsilonlabs.rescli.github.model.Comment;
import org.epsilonlabs.rescli.github.model.Comments;
import org.epsilonlabs.rescli.github.model.Commit;
import org.epsilonlabs.rescli.github.model.CommitActivityStats;
import org.epsilonlabs.rescli.github.model.CommitComments;
import org.epsilonlabs.rescli.github.model.Commits;
import org.epsilonlabs.rescli.github.model.ContentsPath;
import org.epsilonlabs.rescli.github.model.Contributors;
import org.epsilonlabs.rescli.github.model.ContributorsStats;
import org.epsilonlabs.rescli.github.model.DeploymentStatuses;
import org.epsilonlabs.rescli.github.model.Downloads;
import org.epsilonlabs.rescli.github.model.Emojis;
import org.epsilonlabs.rescli.github.model.Event;
import org.epsilonlabs.rescli.github.model.Events;
import org.epsilonlabs.rescli.github.model.Feeds;
import org.epsilonlabs.rescli.github.model.Forks;
import org.epsilonlabs.rescli.github.model.Gist;
import org.epsilonlabs.rescli.github.model.Gists;
import org.epsilonlabs.rescli.github.model.GitignoreLang;
import org.epsilonlabs.rescli.github.model.HeadBranch;
import org.epsilonlabs.rescli.github.model.Hook;
import org.epsilonlabs.rescli.github.model.Issue;
import org.epsilonlabs.rescli.github.model.Issues;
import org.epsilonlabs.rescli.github.model.IssuesComment;
import org.epsilonlabs.rescli.github.model.IssuesComments;
import org.epsilonlabs.rescli.github.model.Keys;
import org.epsilonlabs.rescli.github.model.Label;
import org.epsilonlabs.rescli.github.model.Labels;
import org.epsilonlabs.rescli.github.model.Languages;
import org.epsilonlabs.rescli.github.model.Meta;
import org.epsilonlabs.rescli.github.model.Milestone;
import org.epsilonlabs.rescli.github.model.Notifications;
import org.epsilonlabs.rescli.github.model.Organization;
import org.epsilonlabs.rescli.github.model.ParticipationStats;
import org.epsilonlabs.rescli.github.model.PullRequest;
import org.epsilonlabs.rescli.github.model.Pulls;
import org.epsilonlabs.rescli.github.model.PullsComment;
import org.epsilonlabs.rescli.github.model.RateLimit;
import org.epsilonlabs.rescli.github.model.Ref;
import org.epsilonlabs.rescli.github.model.RefStatus;
import org.epsilonlabs.rescli.github.model.Refs;
import org.epsilonlabs.rescli.github.model.Release;
import org.epsilonlabs.rescli.github.model.Releases;
import org.epsilonlabs.rescli.github.model.Repo;
import org.epsilonlabs.rescli.github.model.RepoComments;
import org.epsilonlabs.rescli.github.model.RepoCommit;
import org.epsilonlabs.rescli.github.model.RepoDeployments;
import org.epsilonlabs.rescli.github.model.Repos;
import org.epsilonlabs.rescli.github.model.Repositories;
import org.epsilonlabs.rescli.github.model.SearchIssuesByKeyword;
import org.epsilonlabs.rescli.github.model.SearchRepositoriesByKeyword;
import org.epsilonlabs.rescli.github.model.SearchUserByEmail;
import org.epsilonlabs.rescli.github.model.SearchUsersByKeyword;
import org.epsilonlabs.rescli.github.model.Subscribition;
import org.epsilonlabs.rescli.github.model.Subscription;
import org.epsilonlabs.rescli.github.model.Tag;
import org.epsilonlabs.rescli.github.model.Tags;
import org.epsilonlabs.rescli.github.model.Team;
import org.epsilonlabs.rescli.github.model.TeamMembership;
import org.epsilonlabs.rescli.github.model.TeamRepos;
import org.epsilonlabs.rescli.github.model.Teams;
import org.epsilonlabs.rescli.github.model.TeamsList;
import org.epsilonlabs.rescli.github.model.Tree;
import org.epsilonlabs.rescli.github.model.User;
import org.epsilonlabs.rescli.github.model.UserKeysKeyId;
import org.epsilonlabs.rescli.github.model.UserUserIdSubscribitions;
import org.epsilonlabs.rescli.github.model.Users;
import org.epsilonlabs.rescli.github.page.GitHubPagination;
import org.epsilonlabs.rescli.github.session.GitHubSession;
import org.epsilonlabs.rescli.github.util.GitHubPropertiesUtil;

public class EntityApi  {

	public static EntityBuilder create(){
		return new EntityBuilder(); 
	}
	
	public static IEntityApi createDefault(){ 
		return new EntityBuilder().setSession(GitHubSession.createPublic()).build(); 
	}
	
	/** BUILDER */
	public static class EntityBuilder 
	implements IClientBuilder<IEntityApi> { 
	
		private String sessionId;
	
		@Override
		public IEntityApi build() {
			return (IEntityApi) new EntityClient(sessionId);
		}
	
		@Override
		public IClientBuilder<IEntityApi> setSession(String sessionId){
			this.sessionId = sessionId;
			return this;
		}
	
		@Override
		public IClientBuilder<IEntityApi> setSession(ISession session){
			this.sessionId = session.id();
			return this;
		}
	}
	
	/** CLIENT */
	private static class EntityClient extends AbstractClient<IEntityEndpoint> 
	implements IEntityApi 
	{
		private GitHubPagination paginationPolicy;
		
		EntityClient(String sessionId) {
			super();

			ExecutorService executor = RateLimitExecutor.create(30, GitHubSession.class, sessionId);
			GitHubInterceptor interceptors = new GitHubInterceptor(sessionId);
			String baseurl = GitHubPropertiesUtil.get(API_BASE_URL);

			if (!baseurl.endsWith("/")) baseurl += "/"; // FIXME Validate in Model with EVL 

			this.client = AbstractClient.okHttp(executor)
					.addInterceptor(interceptors.cacheRequestInterceptor())
					.addInterceptor(interceptors.requestInterceptor())
					.addNetworkInterceptor(interceptors.sessionResponseInterceptor())
					.addNetworkInterceptor(interceptors.cacheResponseInterceptor())
					.build();

			this.callbackEndpoint = AbstractClient.retrofit(client, baseurl).create(IEntityEndpoint.class);
			this.paginationPolicy = GitHubPagination.get();
		}

		/** WRAPED METHODS FOR PAGINATION */
	
		@Override
		public IData<Organization> getOrgsOrganizationByOrg(String org){
			Data<Organization> data = new Data<Organization>();
			data.addElement(callbackEndpoint.getOrgsOrganizationByOrg(org));
			return data;
		}
		
		@Override
		public IData<Emojis> getEmojis(){
			Data<Emojis> data = new Data<Emojis>();
			data.addElement(callbackEndpoint.getEmojis());
			return data;
		}
		
		@Override
		public IDataSet<Repos> getOrgsRepos(String org, String type){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<Users> getReposWatchersUsers(String owner, String repo){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<Gists> getUsersGists(String username, String since){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<Repositories> getRepositories(String since){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<Hook> getReposHooksHook(String owner, String repo){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IData<UserKeysKeyId> getReposKeysUserKeysKeyIdByKeyId(String owner, String repo, Integer keyId){
			Data<UserKeysKeyId> data = new Data<UserKeysKeyId>();
			data.addElement(callbackEndpoint.getReposKeysUserKeysKeyIdByKeyId(owner, repo, keyId));
			return data;
		}
		
		@Override
		public IData<Downloads> getReposDownloads(String owner, String repo){
			Data<Downloads> data = new Data<Downloads>();
			data.addElement(callbackEndpoint.getReposDownloads(owner, repo));
			return data;
		}
		
		@Override
		public IDataSet<IssuesComments> getReposIssuesComments(String owner, String repo, String direction, String sort, String since){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<RepoDeployments> getReposDeploymentsRepoDeployments(String owner, String repo){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IData<Release> getReposReleasesReleaseById(String owner, String repo, String id){
			Data<Release> data = new Data<Release>();
			data.addElement(callbackEndpoint.getReposReleasesReleaseById(owner, repo, id));
			return data;
		}
		
		@Override
		public IDataSet<Assets> getReposReleasesAssets(String owner, String repo, String id){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<Refs> getReposGitRefs(String owner, String repo){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IData<Commit> getReposCommitsCommitByShaCode(String owner, String repo, String shaCode){
			Data<Commit> data = new Data<Commit>();
			data.addElement(callbackEndpoint.getReposCommitsCommitByShaCode(owner, repo, shaCode));
			return data;
		}
		
		@Override
		public IData<Milestone> getReposMilestonesMilestone(String owner, String repo, String state, String direction, String sort){
			Data<Milestone> data = new Data<Milestone>();
			data.addElement(callbackEndpoint.getReposMilestonesMilestone(owner, repo, state, direction, sort));
			return data;
		}
		
		@Override
		public IData<Languages> getReposLanguages(String owner, String repo){
			Data<Languages> data = new Data<Languages>();
			data.addElement(callbackEndpoint.getReposLanguages(owner, repo));
			return data;
		}
		
		@Override
		public IData<Repo> getReposRepoByRepo(String owner, String repo){
			Data<Repo> data = new Data<Repo>();
			data.addElement(callbackEndpoint.getReposRepoByRepo(owner, repo));
			return data;
		}
		
		@Override
		public IData<TeamMembership> getTeamsMembershipsTeamMembershipByUsername(Integer teamId, String username){
			Data<TeamMembership> data = new Data<TeamMembership>();
			data.addElement(callbackEndpoint.getTeamsMembershipsTeamMembershipByUsername(teamId, username));
			return data;
		}
		
		@Override
		public IData<Tag> getReposGitTagsTagByShaCode(String owner, String repo, String shaCode){
			Data<Tag> data = new Data<Tag>();
			data.addElement(callbackEndpoint.getReposGitTagsTagByShaCode(owner, repo, shaCode));
			return data;
		}
		
		@Override
		public IData<ContentsPath> getReposReadmeContentsPath(String owner, String repo, String ref){
			Data<ContentsPath> data = new Data<ContentsPath>();
			data.addElement(callbackEndpoint.getReposReadmeContentsPath(owner, repo, ref));
			return data;
		}
		
		@Override
		public IDataSet<Comments> getGistsComments(Integer id){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<RepoComments> getReposCommentsRepoComments(String owner, String repo){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<Repos> getUserRepos(String type){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IData<SearchUserByEmail> getLegacyUserEmailSearchUserByEmailByEmail(String email){
			Data<SearchUserByEmail> data = new Data<SearchUserByEmail>();
			data.addElement(callbackEndpoint.getLegacyUserEmailSearchUserByEmailByEmail(email));
			return data;
		}
		
		@Override
		public IData<Events> getEvents(){
			Data<Events> data = new Data<Events>();
			data.addElement(callbackEndpoint.getEvents());
			return data;
		}
		
		@Override
		public IData<SearchIssuesByKeyword> getLegacyIssuesSearchSearchIssuesByKeywordByKeyword(String keyword, String state, String owner, String repository){
			Data<SearchIssuesByKeyword> data = new Data<SearchIssuesByKeyword>();
			data.addElement(callbackEndpoint.getLegacyIssuesSearchSearchIssuesByKeywordByKeyword(keyword, state, owner, repository));
			return data;
		}
		
		@Override
		public IDataSet<DeploymentStatuses> getReposDeploymentsStatusesDeploymentStatuses(String owner, String repo, Integer id){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<Pulls> getReposPulls(String owner, String repo, String state, String head, String base){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<Gists> getGists(String since){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<Users> getUserFollowersUsers(){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IData<Gist> getGistsGistById(Integer id){
			Data<Gist> data = new Data<Gist>();
			data.addElement(callbackEndpoint.getGistsGistById(id));
			return data;
		}
		
		@Override
		public IData<Comment> getGistsCommentsCommentByCommentId(Integer id, Integer commentId){
			Data<Comment> data = new Data<Comment>();
			data.addElement(callbackEndpoint.getGistsCommentsCommentByCommentId(id, commentId));
			return data;
		}
		
		@Override
		public IDataSet<Issues> getIssues(String filter, String state, String labels, String sort, String direction, String since){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IData<Event> getReposIssuesEventsEventByEventId(String owner, String repo, Integer eventId){
			Data<Event> data = new Data<Event>();
			data.addElement(callbackEndpoint.getReposIssuesEventsEventByEventId(owner, repo, eventId));
			return data;
		}
		
		@Override
		public IData<Events> getNetworksEvents(String owner, String repo){
			Data<Events> data = new Data<Events>();
			data.addElement(callbackEndpoint.getNetworksEvents(owner, repo));
			return data;
		}
		
		@Override
		public IDataSet<Users> getTeamsMembersUsers(Integer teamId){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<Labels> getReposMilestonesLabels(String owner, String repo, Integer number){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<Users> getOrgsMembersUsers(String org){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<Hook> getReposHooksHookByHookId(String owner, String repo, Integer hookId){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IData<HeadBranch> getReposGitRefsHeadBranchByRef(String owner, String repo, String ref){
			Data<HeadBranch> data = new Data<HeadBranch>();
			data.addElement(callbackEndpoint.getReposGitRefsHeadBranchByRef(owner, repo, ref));
			return data;
		}
		
		@Override
		public IDataSet<Gists> getGistsStarredGists(String since){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<Users> getReposSubscribersUsers(String owner, String repo){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<Users> getReposCollaboratorsUsers(String owner, String repo){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IData<Branch> getReposBranchesBranchByBranch(String owner, String repo, String branch){
			Data<Branch> data = new Data<Branch>();
			data.addElement(callbackEndpoint.getReposBranchesBranchByBranch(owner, repo, branch));
			return data;
		}
		
		@Override
		public IData<Label> getReposLabelsLabelByName(String owner, String repo, String name){
			Data<Label> data = new Data<Label>();
			data.addElement(callbackEndpoint.getReposLabelsLabelByName(owner, repo, name));
			return data;
		}
		
		@Override
		public IData<PullsComment> getReposPullsCommentsPullsComment(String owner, String repo, Integer number){
			Data<PullsComment> data = new Data<PullsComment>();
			data.addElement(callbackEndpoint.getReposPullsCommentsPullsComment(owner, repo, number));
			return data;
		}
		
		@Override
		public IDataSet<Labels> getReposLabels(String owner, String repo){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IData<Subscribition> getReposSubscriptionSubscribition(String owner, String repo){
			Data<Subscribition> data = new Data<Subscribition>();
			data.addElement(callbackEndpoint.getReposSubscriptionSubscribition(owner, repo));
			return data;
		}
		
		@Override
		public IData<Team> getTeamsTeamByTeamId(Integer teamId){
			Data<Team> data = new Data<Team>();
			data.addElement(callbackEndpoint.getTeamsTeamByTeamId(teamId));
			return data;
		}
		
		@Override
		public IData<PullsComment> getReposPullsCommentsPullsCommentByCommentId(String owner, String repo, Integer commentId){
			Data<PullsComment> data = new Data<PullsComment>();
			data.addElement(callbackEndpoint.getReposPullsCommentsPullsCommentByCommentId(owner, repo, commentId));
			return data;
		}
		
		@Override
		public IDataSet<Commits> getReposCommits(String owner, String repo, String since, String sha, String path, String author, String until){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IData<Downloads> getReposDownloadsByDownloadId(String owner, String repo, Integer downloadId){
			Data<Downloads> data = new Data<Downloads>();
			data.addElement(callbackEndpoint.getReposDownloadsByDownloadId(owner, repo, downloadId));
			return data;
		}
		
		@Override
		public IData<Events> getReposIssuesEvents(String owner, String repo, Integer number){
			Data<Events> data = new Data<Events>();
			data.addElement(callbackEndpoint.getReposIssuesEvents(owner, repo, number));
			return data;
		}
		
		@Override
		public IDataSet<Teams> getReposTeams(String owner, String repo){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<IssuesComments> getReposIssuesComments(String owner, String repo, Integer number){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IData<Events> getOrgsEvents(String org){
			Data<Events> data = new Data<Events>();
			data.addElement(callbackEndpoint.getOrgsEvents(org));
			return data;
		}
		
		@Override
		public IDataSet<Users> getOrgsPublic_membersUsers(String org){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IData<Meta> getMeta(){
			Data<Meta> data = new Data<Meta>();
			data.addElement(callbackEndpoint.getMeta());
			return data;
		}
		
		@Override
		public IData<Events> getReposIssuesEvents(String owner, String repo){
			Data<Events> data = new Data<Events>();
			data.addElement(callbackEndpoint.getReposIssuesEvents(owner, repo));
			return data;
		}
		
		@Override
		public IDataSet<CommitActivityStats> getReposStatsCommit_activityCommitActivityStats(String owner, String repo){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<Branches> getReposBranches(String owner, String repo){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<Repos> getUsersRepos(String username, String type){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<Ref> getReposStatusesRefByRef(String owner, String repo, String ref){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IData<SearchRepositoriesByKeyword> getLegacyReposSearchSearchRepositoriesByKeywordByKeyword(String keyword, String order, String language, String startPage, String sort){
			Data<SearchRepositoriesByKeyword> data = new Data<SearchRepositoriesByKeyword>();
			data.addElement(callbackEndpoint.getLegacyReposSearchSearchRepositoriesByKeywordByKeyword(keyword, order, language, startPage, sort));
			return data;
		}
		
		@Override
		public IData<Milestone> getReposMilestonesMilestoneByNumber(String owner, String repo, Integer number){
			Data<Milestone> data = new Data<Milestone>();
			data.addElement(callbackEndpoint.getReposMilestonesMilestoneByNumber(owner, repo, number));
			return data;
		}
		
		@Override
		public IData<Notifications> getReposNotifications(String owner, String repo, Boolean all, Boolean participating, String since){
			Data<Notifications> data = new Data<Notifications>();
			data.addElement(callbackEndpoint.getReposNotifications(owner, repo, all, participating, since));
			return data;
		}
		
		@Override
		public IData<Tags> getReposTags(String owner, String repo){
			Data<Tags> data = new Data<Tags>();
			data.addElement(callbackEndpoint.getReposTags(owner, repo));
			return data;
		}
		
		@Override
		public IDataSet<Gists> getGistsPublicGists(String since){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<Issues> getUserIssues(String filter, String state, String labels, String sort, String direction, String since){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<TeamsList> getUserTeamsTeamsList(){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<UserUserIdSubscribitions> getUserSubscriptionsUserUserIdSubscribitions(){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IData<Notifications> getNotificationsThreadsNotificationsById(Integer id){
			Data<Notifications> data = new Data<Notifications>();
			data.addElement(callbackEndpoint.getNotificationsThreadsNotificationsById(id));
			return data;
		}
		
		@Override
		public IData<Notifications> getNotifications(Boolean all, Boolean participating, String since){
			Data<Notifications> data = new Data<Notifications>();
			data.addElement(callbackEndpoint.getNotifications(all, participating, since));
			return data;
		}
		
		@Override
		public IDataSet<Releases> getReposReleases(String owner, String repo){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IData<PullRequest> getReposPullsPullRequestByNumber(String owner, String repo, Integer number){
			Data<PullRequest> data = new Data<PullRequest>();
			data.addElement(callbackEndpoint.getReposPullsPullRequestByNumber(owner, repo, number));
			return data;
		}
		
		@Override
		public IDataSet<Users> getReposStargazersUsers(String owner, String repo){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<Integer> getReposStatsCode_frequencyCodeFrequencyStats(String owner, String repo){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<Assignees> getReposAssignees(String owner, String repo){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<Users> getUserFollowingUsers(){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IData<GitignoreLang> getGitignoreTemplatesGitignoreLangByLanguage(String language){
			Data<GitignoreLang> data = new Data<GitignoreLang>();
			data.addElement(callbackEndpoint.getGitignoreTemplatesGitignoreLangByLanguage(language));
			return data;
		}
		
		@Override
		public IDataSet<Issues> getReposIssues(String owner, String repo, String filter, String state, String labels, String sort, String direction, String since){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IData<CommitComments> getReposCommentsCommitCommentsByCommentId(String owner, String repo, Integer commentId){
			Data<CommitComments> data = new Data<CommitComments>();
			data.addElement(callbackEndpoint.getReposCommentsCommitCommentsByCommentId(owner, repo, commentId));
			return data;
		}
		
		@Override
		public IDataSet<Pulls> getReposPullsFilesPulls(String owner, String repo, Integer number){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IData<ContentsPath> getReposContentsContentsPathByPath(String owner, String repo, String path, String path_0, String ref){
			Data<ContentsPath> data = new Data<ContentsPath>();
			data.addElement(callbackEndpoint.getReposContentsContentsPathByPath(owner, repo, path, path_0, ref));
			return data;
		}
		
		@Override
		public IData<RateLimit> getRate_limitRateLimit(){
			Data<RateLimit> data = new Data<RateLimit>();
			data.addElement(callbackEndpoint.getRate_limitRateLimit());
			return data;
		}
		
		@Override
		public IData<User> getUser(){
			Data<User> data = new Data<User>();
			data.addElement(callbackEndpoint.getUser());
			return data;
		}
		
		@Override
		public IDataSet<Integer> getReposStatsPunch_cardCodeFrequencyStats(String owner, String repo){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IData<ParticipationStats> getReposStatsParticipationParticipationStats(String owner, String repo){
			Data<ParticipationStats> data = new Data<ParticipationStats>();
			data.addElement(callbackEndpoint.getReposStatsParticipationParticipationStats(owner, repo));
			return data;
		}
		
		@Override
		public IDataSet<ContributorsStats> getReposStatsContributorsContributorsStats(String owner, String repo){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<Users> getUsersByUsername(String username){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<String> getUserEmails(){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IData<Events> getReposEvents(String owner, String repo){
			Data<Events> data = new Data<Events>();
			data.addElement(callbackEndpoint.getReposEvents(owner, repo));
			return data;
		}
		
		@Override
		public IData<RepoCommit> getReposGitCommitsRepoCommitByShaCode(String owner, String repo, String shaCode){
			Data<RepoCommit> data = new Data<RepoCommit>();
			data.addElement(callbackEndpoint.getReposGitCommitsRepoCommitByShaCode(owner, repo, shaCode));
			return data;
		}
		
		@Override
		public IData<Feeds> getFeeds(){
			Data<Feeds> data = new Data<Feeds>();
			data.addElement(callbackEndpoint.getFeeds());
			return data;
		}
		
		@Override
		public IData<Issue> getReposIssuesIssueByNumber(String owner, String repo, Integer number){
			Data<Issue> data = new Data<Issue>();
			data.addElement(callbackEndpoint.getReposIssuesIssueByNumber(owner, repo, number));
			return data;
		}
		
		@Override
		public IDataSet<Issues> getOrgsIssues(String org, String filter, String state, String labels, String sort, String direction, String since){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<Users> getUsersFollowersUsers(String username){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<Contributors> getReposContributors(String owner, String repo, String anon){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<IssuesComments> getReposPullsCommentsIssuesComments(String owner, String repo, String direction, String sort, String since){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<Labels> getReposIssuesLabels(String owner, String repo, Integer number){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<Commits> getReposPullsCommits(String owner, String repo, Integer number){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<RefStatus> getReposCommitsStatusRefStatus(String owner, String repo, String ref){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<RepoComments> getReposCommitsCommentsRepoComments(String owner, String repo, String shaCode){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IDataSet<Users> getUsers(Integer since){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IData<Blob> getReposGitBlobsBlobByShaCode(String owner, String repo, String shaCode){
			Data<Blob> data = new Data<Blob>();
			data.addElement(callbackEndpoint.getReposGitBlobsBlobByShaCode(owner, repo, shaCode));
			return data;
		}
		
		@Override
		public IDataSet<TeamRepos> getTeamsReposTeamRepos(Integer teamId){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IData<UserKeysKeyId> getUserKeysUserKeysKeyIdByKeyId(Integer keyId){
			Data<UserKeysKeyId> data = new Data<UserKeysKeyId>();
			data.addElement(callbackEndpoint.getUserKeysUserKeysKeyIdByKeyId(keyId));
			return data;
		}
		
		@Override
		public IDataSet<Forks> getReposForks(String owner, String repo, String sort){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IData<SearchUsersByKeyword> getLegacyUserSearchSearchUsersByKeywordByKeyword(String keyword, String order, String startPage, String sort){
			Data<SearchUsersByKeyword> data = new Data<SearchUsersByKeyword>();
			data.addElement(callbackEndpoint.getLegacyUserSearchSearchUsersByKeywordByKeyword(keyword, order, startPage, sort));
			return data;
		}
		
		@Override
		public IData<IssuesComment> getReposIssuesCommentsIssuesCommentByCommentId(String owner, String repo, Integer commentId){
			Data<IssuesComment> data = new Data<IssuesComment>();
			data.addElement(callbackEndpoint.getReposIssuesCommentsIssuesCommentByCommentId(owner, repo, commentId));
			return data;
		}
		
		@Override
		public IData<Asset> getReposReleasesAssetsAssetById(String owner, String repo, String id){
			Data<Asset> data = new Data<Asset>();
			data.addElement(callbackEndpoint.getReposReleasesAssetsAssetById(owner, repo, id));
			return data;
		}
		
		@Override
		public IDataSet<Teams> getOrgsTeams(String org){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IData<Tree> getReposGitTreesTreeByShaCode(String owner, String repo, String shaCode, Integer recursive){
			Data<Tree> data = new Data<Tree>();
			data.addElement(callbackEndpoint.getReposGitTreesTreeByShaCode(owner, repo, shaCode, recursive));
			return data;
		}
		
		@Override
		public IDataSet<Keys> getReposKeys(String owner, String repo){
			return null; // TODO (FIXME) Add support for arrays!! 
		}
		
		@Override
		public IData<Subscription> getNotificationsThreadsSubscription(Integer id){
			Data<Subscription> data = new Data<Subscription>();
			data.addElement(callbackEndpoint.getNotificationsThreadsSubscription(id));
			return data;
		}
		
		
	}
}
