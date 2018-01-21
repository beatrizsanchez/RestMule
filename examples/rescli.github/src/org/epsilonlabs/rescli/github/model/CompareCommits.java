package org.epsilonlabs.rescli.github.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CompareCommits {

	@JsonProperty("patch_url") 
	private String patchUrl;
	
	@JsonProperty("behind_by") 
	private Integer behindBy;
	
	@JsonProperty("html_url") 
	private String htmlUrl;
	
	@JsonProperty("diff_url") 
	private String diffUrl;
	
	@JsonProperty("ahead_by") 
	private Integer aheadBy;
	
	@JsonProperty("permalink_url") 
	private String permalinkUrl;
	
	@JsonProperty("url") 
	private String url;
	
	@JsonProperty("status") 
	private String status;
	
	@JsonProperty("total_commits") 
	private Integer totalCommits;
	
	@JsonProperty("base_commit") 
	private BaseCommit baseCommit;
	
	@JsonProperty("commits") 
	private List<Commits> commits = new ArrayList<Commits>();
	
	@JsonProperty("files") 
	private List<Files> files = new ArrayList<Files>();
	
	public String getPatchUrl() {
		return this.patchUrl;
	}
	
	public Integer getBehindBy() {
		return this.behindBy;
	}
	
	public String getHtmlUrl() {
		return this.htmlUrl;
	}
	
	public String getDiffUrl() {
		return this.diffUrl;
	}
	
	public Integer getAheadBy() {
		return this.aheadBy;
	}
	
	public String getPermalinkUrl() {
		return this.permalinkUrl;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public String getStatus() {
		return this.status;
	}
	
	public Integer getTotalCommits() {
		return this.totalCommits;
	}
	
	public BaseCommit getBaseCommit() {
		return this.baseCommit;
	}
	
	public List<Commits> getCommits() {
		return this.commits;
	}
	
	public List<Files> getFiles() {
		return this.files;
	}
	
	@Override
	public String toString() {
		return "CompareCommits [ "
			+ "patchUrl = " + this.patchUrl + ", "
			+ "behindBy = " + this.behindBy + ", "
			+ "htmlUrl = " + this.htmlUrl + ", "
			+ "diffUrl = " + this.diffUrl + ", "
			+ "aheadBy = " + this.aheadBy + ", "
			+ "permalinkUrl = " + this.permalinkUrl + ", "
			+ "url = " + this.url + ", "
			+ "status = " + this.status + ", "
			+ "totalCommits = " + this.totalCommits + ", "
			+ "baseCommit = " + this.baseCommit + ", "
			+ "commits = " + this.commits + ", "
			+ "files = " + this.files + ", "
			+ "]"; 
	}	
	public class BaseCommit {
	
		@JsonProperty("sha") 
		private String sha;
		
		@JsonProperty("url") 
		private String url;
		
		@JsonProperty("committer") 
		private CommitterInner committerInner;
		
		@JsonProperty("author") 
		private Author author;
		
		@JsonProperty("commit") 
		private Commit commit;
		
		@JsonProperty("parents") 
		private List<ParentsInner> parents = new ArrayList<ParentsInner>();
		
		public String getSha() {
			return this.sha;
		}
		
		public String getUrl() {
			return this.url;
		}
		
		public CommitterInner getCommitterInner() {
			return this.committerInner;
		}
		
		public Author getAuthor() {
			return this.author;
		}
		
		public Commit getCommit() {
			return this.commit;
		}
		
		public List<ParentsInner> getParents() {
			return this.parents;
		}
		
		@Override
		public String toString() {
			return "BaseCommit [ "
				+ "sha = " + this.sha + ", "
				+ "url = " + this.url + ", "
				+ "committerInner = " + this.committerInner + ", "
				+ "author = " + this.author + ", "
				+ "commit = " + this.commit + ", "
				+ "parents = " + this.parents + ", "
				+ "]"; 
		}	
		public class CommitterInner {
		
			@JsonProperty("gists_url") 
			private String gistsUrl;
			
			@JsonProperty("repos_url") 
			private String reposUrl;
			
			@JsonProperty("following_url") 
			private String followingUrl;
			
			@JsonProperty("starred_url") 
			private String starredUrl;
			
			@JsonProperty("followers_url") 
			private String followersUrl;
			
			@JsonProperty("login") 
			private String login;
			
			@JsonProperty("type") 
			private String type;
			
			@JsonProperty("url") 
			private String url;
			
			@JsonProperty("subscriptions_url") 
			private String subscriptionsUrl;
			
			@JsonProperty("received_events_url") 
			private String receivedEventsUrl;
			
			@JsonProperty("avatar_url") 
			private String avatarUrl;
			
			@JsonProperty("events_url") 
			private String eventsUrl;
			
			@JsonProperty("html_url") 
			private String htmlUrl;
			
			@JsonProperty("site_admin") 
			private Boolean siteAdmin;
			
			@JsonProperty("id") 
			private Integer id;
			
			@JsonProperty("gravatar_id") 
			private String gravatarId;
			
			@JsonProperty("organizations_url") 
			private String organizationsUrl;
			
			public String getGistsUrl() {
				return this.gistsUrl;
			}
			
			public String getReposUrl() {
				return this.reposUrl;
			}
			
			public String getFollowingUrl() {
				return this.followingUrl;
			}
			
			public String getStarredUrl() {
				return this.starredUrl;
			}
			
			public String getFollowersUrl() {
				return this.followersUrl;
			}
			
			public String getLogin() {
				return this.login;
			}
			
			public String getType() {
				return this.type;
			}
			
			public String getUrl() {
				return this.url;
			}
			
			public String getSubscriptionsUrl() {
				return this.subscriptionsUrl;
			}
			
			public String getReceivedEventsUrl() {
				return this.receivedEventsUrl;
			}
			
			public String getAvatarUrl() {
				return this.avatarUrl;
			}
			
			public String getEventsUrl() {
				return this.eventsUrl;
			}
			
			public String getHtmlUrl() {
				return this.htmlUrl;
			}
			
			public Boolean getSiteAdmin() {
				return this.siteAdmin;
			}
			
			public Integer getId() {
				return this.id;
			}
			
			public String getGravatarId() {
				return this.gravatarId;
			}
			
			public String getOrganizationsUrl() {
				return this.organizationsUrl;
			}
			
			@Override
			public String toString() {
				return "CommitterInner [ "
					+ "gistsUrl = " + this.gistsUrl + ", "
					+ "reposUrl = " + this.reposUrl + ", "
					+ "followingUrl = " + this.followingUrl + ", "
					+ "starredUrl = " + this.starredUrl + ", "
					+ "followersUrl = " + this.followersUrl + ", "
					+ "login = " + this.login + ", "
					+ "type = " + this.type + ", "
					+ "url = " + this.url + ", "
					+ "subscriptionsUrl = " + this.subscriptionsUrl + ", "
					+ "receivedEventsUrl = " + this.receivedEventsUrl + ", "
					+ "avatarUrl = " + this.avatarUrl + ", "
					+ "eventsUrl = " + this.eventsUrl + ", "
					+ "htmlUrl = " + this.htmlUrl + ", "
					+ "siteAdmin = " + this.siteAdmin + ", "
					+ "id = " + this.id + ", "
					+ "gravatarId = " + this.gravatarId + ", "
					+ "organizationsUrl = " + this.organizationsUrl + ", "
					+ "]"; 
			}	
		}
		
		public class Author {
		
			@JsonProperty("gists_url") 
			private String gistsUrl;
			
			@JsonProperty("repos_url") 
			private String reposUrl;
			
			@JsonProperty("following_url") 
			private String followingUrl;
			
			@JsonProperty("starred_url") 
			private String starredUrl;
			
			@JsonProperty("followers_url") 
			private String followersUrl;
			
			@JsonProperty("login") 
			private String login;
			
			@JsonProperty("type") 
			private String type;
			
			@JsonProperty("url") 
			private String url;
			
			@JsonProperty("subscriptions_url") 
			private String subscriptionsUrl;
			
			@JsonProperty("received_events_url") 
			private String receivedEventsUrl;
			
			@JsonProperty("avatar_url") 
			private String avatarUrl;
			
			@JsonProperty("events_url") 
			private String eventsUrl;
			
			@JsonProperty("html_url") 
			private String htmlUrl;
			
			@JsonProperty("site_admin") 
			private Boolean siteAdmin;
			
			@JsonProperty("id") 
			private Integer id;
			
			@JsonProperty("gravatar_id") 
			private String gravatarId;
			
			@JsonProperty("organizations_url") 
			private String organizationsUrl;
			
			public String getGistsUrl() {
				return this.gistsUrl;
			}
			
			public String getReposUrl() {
				return this.reposUrl;
			}
			
			public String getFollowingUrl() {
				return this.followingUrl;
			}
			
			public String getStarredUrl() {
				return this.starredUrl;
			}
			
			public String getFollowersUrl() {
				return this.followersUrl;
			}
			
			public String getLogin() {
				return this.login;
			}
			
			public String getType() {
				return this.type;
			}
			
			public String getUrl() {
				return this.url;
			}
			
			public String getSubscriptionsUrl() {
				return this.subscriptionsUrl;
			}
			
			public String getReceivedEventsUrl() {
				return this.receivedEventsUrl;
			}
			
			public String getAvatarUrl() {
				return this.avatarUrl;
			}
			
			public String getEventsUrl() {
				return this.eventsUrl;
			}
			
			public String getHtmlUrl() {
				return this.htmlUrl;
			}
			
			public Boolean getSiteAdmin() {
				return this.siteAdmin;
			}
			
			public Integer getId() {
				return this.id;
			}
			
			public String getGravatarId() {
				return this.gravatarId;
			}
			
			public String getOrganizationsUrl() {
				return this.organizationsUrl;
			}
			
			@Override
			public String toString() {
				return "Author [ "
					+ "gistsUrl = " + this.gistsUrl + ", "
					+ "reposUrl = " + this.reposUrl + ", "
					+ "followingUrl = " + this.followingUrl + ", "
					+ "starredUrl = " + this.starredUrl + ", "
					+ "followersUrl = " + this.followersUrl + ", "
					+ "login = " + this.login + ", "
					+ "type = " + this.type + ", "
					+ "url = " + this.url + ", "
					+ "subscriptionsUrl = " + this.subscriptionsUrl + ", "
					+ "receivedEventsUrl = " + this.receivedEventsUrl + ", "
					+ "avatarUrl = " + this.avatarUrl + ", "
					+ "eventsUrl = " + this.eventsUrl + ", "
					+ "htmlUrl = " + this.htmlUrl + ", "
					+ "siteAdmin = " + this.siteAdmin + ", "
					+ "id = " + this.id + ", "
					+ "gravatarId = " + this.gravatarId + ", "
					+ "organizationsUrl = " + this.organizationsUrl + ", "
					+ "]"; 
			}	
		}
		
		public class Commit {
		
			@JsonProperty("message") 
			private String message;
			
			@JsonProperty("url") 
			private String url;
			
			@JsonProperty("committer") 
			private CommitterInnerInnerInner committerInnerInnerInner;
			
			@JsonProperty("author") 
			private AuthorInnerInnerInner authorInnerInnerInner;
			
			@JsonProperty("tree") 
			private Tree tree;
			
			public String getMessage() {
				return this.message;
			}
			
			public String getUrl() {
				return this.url;
			}
			
			public CommitterInnerInnerInner getCommitterInnerInnerInner() {
				return this.committerInnerInnerInner;
			}
			
			public AuthorInnerInnerInner getAuthorInnerInnerInner() {
				return this.authorInnerInnerInner;
			}
			
			public Tree getTree() {
				return this.tree;
			}
			
			@Override
			public String toString() {
				return "Commit [ "
					+ "message = " + this.message + ", "
					+ "url = " + this.url + ", "
					+ "committerInnerInnerInner = " + this.committerInnerInnerInner + ", "
					+ "authorInnerInnerInner = " + this.authorInnerInnerInner + ", "
					+ "tree = " + this.tree + ", "
					+ "]"; 
			}	
			public class CommitterInnerInnerInner {
			
				@JsonProperty("date") 
				private String date;
				
				@JsonProperty("name") 
				private String name;
				
				@JsonProperty("email") 
				private String email;
				
				public String getDate() {
					return this.date;
				}
				
				public String getName() {
					return this.name;
				}
				
				public String getEmail() {
					return this.email;
				}
				
				@Override
				public String toString() {
					return "CommitterInnerInnerInner [ "
						+ "date = " + this.date + ", "
						+ "name = " + this.name + ", "
						+ "email = " + this.email + ", "
						+ "]"; 
				}	
			}
			
			public class AuthorInnerInnerInner {
			
				@JsonProperty("date") 
				private String date;
				
				@JsonProperty("name") 
				private String name;
				
				@JsonProperty("email") 
				private String email;
				
				public String getDate() {
					return this.date;
				}
				
				public String getName() {
					return this.name;
				}
				
				public String getEmail() {
					return this.email;
				}
				
				@Override
				public String toString() {
					return "AuthorInnerInnerInner [ "
						+ "date = " + this.date + ", "
						+ "name = " + this.name + ", "
						+ "email = " + this.email + ", "
						+ "]"; 
				}	
			}
			
			public class Tree {
			
				@JsonProperty("sha") 
				private String sha;
				
				@JsonProperty("url") 
				private String url;
				
				public String getSha() {
					return this.sha;
				}
				
				public String getUrl() {
					return this.url;
				}
				
				@Override
				public String toString() {
					return "Tree [ "
						+ "sha = " + this.sha + ", "
						+ "url = " + this.url + ", "
						+ "]"; 
				}	
			}
			
		}
		
		public class ParentsInner {
		
			@JsonProperty("sha") 
			private String sha;
			
			@JsonProperty("url") 
			private String url;
			
			public String getSha() {
				return this.sha;
			}
			
			public String getUrl() {
				return this.url;
			}
			
			@Override
			public String toString() {
				return "ParentsInner [ "
					+ "sha = " + this.sha + ", "
					+ "url = " + this.url + ", "
					+ "]"; 
			}	
		}
		
	}
	
	public class Files {
	
		@JsonProperty("patch") 
		private String patch;
		
		@JsonProperty("filename") 
		private String filename;
		
		@JsonProperty("additions") 
		private Integer additions;
		
		@JsonProperty("deletions") 
		private Integer deletions;
		
		@JsonProperty("changes") 
		private Integer changes;
		
		@JsonProperty("blob_url") 
		private String blobUrl;
		
		@JsonProperty("sha") 
		private String sha;
		
		@JsonProperty("raw_url") 
		private String rawUrl;
		
		@JsonProperty("contents_url") 
		private String contentsUrl;
		
		@JsonProperty("status") 
		private String status;
		
		public String getPatch() {
			return this.patch;
		}
		
		public String getFilename() {
			return this.filename;
		}
		
		public Integer getAdditions() {
			return this.additions;
		}
		
		public Integer getDeletions() {
			return this.deletions;
		}
		
		public Integer getChanges() {
			return this.changes;
		}
		
		public String getBlobUrl() {
			return this.blobUrl;
		}
		
		public String getSha() {
			return this.sha;
		}
		
		public String getRawUrl() {
			return this.rawUrl;
		}
		
		public String getContentsUrl() {
			return this.contentsUrl;
		}
		
		public String getStatus() {
			return this.status;
		}
		
		@Override
		public String toString() {
			return "Files [ "
				+ "patch = " + this.patch + ", "
				+ "filename = " + this.filename + ", "
				+ "additions = " + this.additions + ", "
				+ "deletions = " + this.deletions + ", "
				+ "changes = " + this.changes + ", "
				+ "blobUrl = " + this.blobUrl + ", "
				+ "sha = " + this.sha + ", "
				+ "rawUrl = " + this.rawUrl + ", "
				+ "contentsUrl = " + this.contentsUrl + ", "
				+ "status = " + this.status + ", "
				+ "]"; 
		}	
	}
	
	public class Commits {
	
		@JsonProperty("sha") 
		private String sha;
		
		@JsonProperty("url") 
		private String url;
		
		@JsonProperty("committer") 
		private Committer committer;
		
		@JsonProperty("author") 
		private AuthorInner authorInner;
		
		@JsonProperty("commit") 
		private CommitInner commitInner;
		
		@JsonProperty("parents") 
		private List<Parents> parents = new ArrayList<Parents>();
		
		public String getSha() {
			return this.sha;
		}
		
		public String getUrl() {
			return this.url;
		}
		
		public Committer getCommitter() {
			return this.committer;
		}
		
		public AuthorInner getAuthorInner() {
			return this.authorInner;
		}
		
		public CommitInner getCommitInner() {
			return this.commitInner;
		}
		
		public List<Parents> getParents() {
			return this.parents;
		}
		
		@Override
		public String toString() {
			return "Commits [ "
				+ "sha = " + this.sha + ", "
				+ "url = " + this.url + ", "
				+ "committer = " + this.committer + ", "
				+ "authorInner = " + this.authorInner + ", "
				+ "commitInner = " + this.commitInner + ", "
				+ "parents = " + this.parents + ", "
				+ "]"; 
		}	
		public class Committer {
		
			@JsonProperty("gists_url") 
			private String gistsUrl;
			
			@JsonProperty("repos_url") 
			private String reposUrl;
			
			@JsonProperty("following_url") 
			private String followingUrl;
			
			@JsonProperty("starred_url") 
			private String starredUrl;
			
			@JsonProperty("followers_url") 
			private String followersUrl;
			
			@JsonProperty("login") 
			private String login;
			
			@JsonProperty("type") 
			private String type;
			
			@JsonProperty("url") 
			private String url;
			
			@JsonProperty("subscriptions_url") 
			private String subscriptionsUrl;
			
			@JsonProperty("received_events_url") 
			private String receivedEventsUrl;
			
			@JsonProperty("avatar_url") 
			private String avatarUrl;
			
			@JsonProperty("events_url") 
			private String eventsUrl;
			
			@JsonProperty("html_url") 
			private String htmlUrl;
			
			@JsonProperty("site_admin") 
			private Boolean siteAdmin;
			
			@JsonProperty("id") 
			private Integer id;
			
			@JsonProperty("gravatar_id") 
			private String gravatarId;
			
			@JsonProperty("organizations_url") 
			private String organizationsUrl;
			
			public String getGistsUrl() {
				return this.gistsUrl;
			}
			
			public String getReposUrl() {
				return this.reposUrl;
			}
			
			public String getFollowingUrl() {
				return this.followingUrl;
			}
			
			public String getStarredUrl() {
				return this.starredUrl;
			}
			
			public String getFollowersUrl() {
				return this.followersUrl;
			}
			
			public String getLogin() {
				return this.login;
			}
			
			public String getType() {
				return this.type;
			}
			
			public String getUrl() {
				return this.url;
			}
			
			public String getSubscriptionsUrl() {
				return this.subscriptionsUrl;
			}
			
			public String getReceivedEventsUrl() {
				return this.receivedEventsUrl;
			}
			
			public String getAvatarUrl() {
				return this.avatarUrl;
			}
			
			public String getEventsUrl() {
				return this.eventsUrl;
			}
			
			public String getHtmlUrl() {
				return this.htmlUrl;
			}
			
			public Boolean getSiteAdmin() {
				return this.siteAdmin;
			}
			
			public Integer getId() {
				return this.id;
			}
			
			public String getGravatarId() {
				return this.gravatarId;
			}
			
			public String getOrganizationsUrl() {
				return this.organizationsUrl;
			}
			
			@Override
			public String toString() {
				return "Committer [ "
					+ "gistsUrl = " + this.gistsUrl + ", "
					+ "reposUrl = " + this.reposUrl + ", "
					+ "followingUrl = " + this.followingUrl + ", "
					+ "starredUrl = " + this.starredUrl + ", "
					+ "followersUrl = " + this.followersUrl + ", "
					+ "login = " + this.login + ", "
					+ "type = " + this.type + ", "
					+ "url = " + this.url + ", "
					+ "subscriptionsUrl = " + this.subscriptionsUrl + ", "
					+ "receivedEventsUrl = " + this.receivedEventsUrl + ", "
					+ "avatarUrl = " + this.avatarUrl + ", "
					+ "eventsUrl = " + this.eventsUrl + ", "
					+ "htmlUrl = " + this.htmlUrl + ", "
					+ "siteAdmin = " + this.siteAdmin + ", "
					+ "id = " + this.id + ", "
					+ "gravatarId = " + this.gravatarId + ", "
					+ "organizationsUrl = " + this.organizationsUrl + ", "
					+ "]"; 
			}	
		}
		
		public class AuthorInner {
		
			@JsonProperty("gists_url") 
			private String gistsUrl;
			
			@JsonProperty("repos_url") 
			private String reposUrl;
			
			@JsonProperty("following_url") 
			private String followingUrl;
			
			@JsonProperty("starred_url") 
			private String starredUrl;
			
			@JsonProperty("followers_url") 
			private String followersUrl;
			
			@JsonProperty("login") 
			private String login;
			
			@JsonProperty("type") 
			private String type;
			
			@JsonProperty("url") 
			private String url;
			
			@JsonProperty("subscriptions_url") 
			private String subscriptionsUrl;
			
			@JsonProperty("received_events_url") 
			private String receivedEventsUrl;
			
			@JsonProperty("avatar_url") 
			private String avatarUrl;
			
			@JsonProperty("events_url") 
			private String eventsUrl;
			
			@JsonProperty("html_url") 
			private String htmlUrl;
			
			@JsonProperty("site_admin") 
			private Boolean siteAdmin;
			
			@JsonProperty("id") 
			private Integer id;
			
			@JsonProperty("gravatar_id") 
			private String gravatarId;
			
			@JsonProperty("organizations_url") 
			private String organizationsUrl;
			
			public String getGistsUrl() {
				return this.gistsUrl;
			}
			
			public String getReposUrl() {
				return this.reposUrl;
			}
			
			public String getFollowingUrl() {
				return this.followingUrl;
			}
			
			public String getStarredUrl() {
				return this.starredUrl;
			}
			
			public String getFollowersUrl() {
				return this.followersUrl;
			}
			
			public String getLogin() {
				return this.login;
			}
			
			public String getType() {
				return this.type;
			}
			
			public String getUrl() {
				return this.url;
			}
			
			public String getSubscriptionsUrl() {
				return this.subscriptionsUrl;
			}
			
			public String getReceivedEventsUrl() {
				return this.receivedEventsUrl;
			}
			
			public String getAvatarUrl() {
				return this.avatarUrl;
			}
			
			public String getEventsUrl() {
				return this.eventsUrl;
			}
			
			public String getHtmlUrl() {
				return this.htmlUrl;
			}
			
			public Boolean getSiteAdmin() {
				return this.siteAdmin;
			}
			
			public Integer getId() {
				return this.id;
			}
			
			public String getGravatarId() {
				return this.gravatarId;
			}
			
			public String getOrganizationsUrl() {
				return this.organizationsUrl;
			}
			
			@Override
			public String toString() {
				return "AuthorInner [ "
					+ "gistsUrl = " + this.gistsUrl + ", "
					+ "reposUrl = " + this.reposUrl + ", "
					+ "followingUrl = " + this.followingUrl + ", "
					+ "starredUrl = " + this.starredUrl + ", "
					+ "followersUrl = " + this.followersUrl + ", "
					+ "login = " + this.login + ", "
					+ "type = " + this.type + ", "
					+ "url = " + this.url + ", "
					+ "subscriptionsUrl = " + this.subscriptionsUrl + ", "
					+ "receivedEventsUrl = " + this.receivedEventsUrl + ", "
					+ "avatarUrl = " + this.avatarUrl + ", "
					+ "eventsUrl = " + this.eventsUrl + ", "
					+ "htmlUrl = " + this.htmlUrl + ", "
					+ "siteAdmin = " + this.siteAdmin + ", "
					+ "id = " + this.id + ", "
					+ "gravatarId = " + this.gravatarId + ", "
					+ "organizationsUrl = " + this.organizationsUrl + ", "
					+ "]"; 
			}	
		}
		
		public class CommitInner {
		
			@JsonProperty("message") 
			private String message;
			
			@JsonProperty("url") 
			private String url;
			
			@JsonProperty("committer") 
			private CommitterInnerInner committerInnerInner;
			
			@JsonProperty("author") 
			private AuthorInnerInner authorInnerInner;
			
			@JsonProperty("tree") 
			private TreeInner treeInner;
			
			public String getMessage() {
				return this.message;
			}
			
			public String getUrl() {
				return this.url;
			}
			
			public CommitterInnerInner getCommitterInnerInner() {
				return this.committerInnerInner;
			}
			
			public AuthorInnerInner getAuthorInnerInner() {
				return this.authorInnerInner;
			}
			
			public TreeInner getTreeInner() {
				return this.treeInner;
			}
			
			@Override
			public String toString() {
				return "CommitInner [ "
					+ "message = " + this.message + ", "
					+ "url = " + this.url + ", "
					+ "committerInnerInner = " + this.committerInnerInner + ", "
					+ "authorInnerInner = " + this.authorInnerInner + ", "
					+ "treeInner = " + this.treeInner + ", "
					+ "]"; 
			}	
			public class CommitterInnerInner {
			
				@JsonProperty("date") 
				private String date;
				
				@JsonProperty("name") 
				private String name;
				
				@JsonProperty("email") 
				private String email;
				
				public String getDate() {
					return this.date;
				}
				
				public String getName() {
					return this.name;
				}
				
				public String getEmail() {
					return this.email;
				}
				
				@Override
				public String toString() {
					return "CommitterInnerInner [ "
						+ "date = " + this.date + ", "
						+ "name = " + this.name + ", "
						+ "email = " + this.email + ", "
						+ "]"; 
				}	
			}
			
			public class AuthorInnerInner {
			
				@JsonProperty("date") 
				private String date;
				
				@JsonProperty("name") 
				private String name;
				
				@JsonProperty("email") 
				private String email;
				
				public String getDate() {
					return this.date;
				}
				
				public String getName() {
					return this.name;
				}
				
				public String getEmail() {
					return this.email;
				}
				
				@Override
				public String toString() {
					return "AuthorInnerInner [ "
						+ "date = " + this.date + ", "
						+ "name = " + this.name + ", "
						+ "email = " + this.email + ", "
						+ "]"; 
				}	
			}
			
			public class TreeInner {
			
				@JsonProperty("sha") 
				private String sha;
				
				@JsonProperty("url") 
				private String url;
				
				public String getSha() {
					return this.sha;
				}
				
				public String getUrl() {
					return this.url;
				}
				
				@Override
				public String toString() {
					return "TreeInner [ "
						+ "sha = " + this.sha + ", "
						+ "url = " + this.url + ", "
						+ "]"; 
				}	
			}
			
		}
		
		public class Parents {
		
			@JsonProperty("sha") 
			private String sha;
			
			@JsonProperty("url") 
			private String url;
			
			public String getSha() {
				return this.sha;
			}
			
			public String getUrl() {
				return this.url;
			}
			
			@Override
			public String toString() {
				return "Parents [ "
					+ "sha = " + this.sha + ", "
					+ "url = " + this.url + ", "
					+ "]"; 
			}	
		}
		
	}
	
}	
